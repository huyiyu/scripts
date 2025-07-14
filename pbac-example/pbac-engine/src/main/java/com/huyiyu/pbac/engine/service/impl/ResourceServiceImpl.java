package com.huyiyu.pbac.engine.service.impl;

import static com.huyiyu.pbac.core.constant.PbacConstant.PBAC_PATH_PREFIX;
import static com.huyiyu.pbac.engine.constant.PbacCacheConstant.PBAC_FUZZY_RESOURCE;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.core.domain.PbacResource;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.enums.MatchType;
import com.huyiyu.pbac.core.exception.BusiPbacException;
import com.huyiyu.pbac.engine.convert.PbacConvertor;
import com.huyiyu.pbac.engine.entity.Resource;
import com.huyiyu.pbac.engine.mapper.ResourceMapper;
import com.huyiyu.pbac.engine.service.IPolicyRuleService;
import com.huyiyu.pbac.engine.service.IResourceService;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

/**
 * <p>
 * 请求客户端资源 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements
    IResourceService {

  private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();


  private final IPolicyRuleService policyRuleService;

  private final RedisTemplate redisTemplate;


  private Optional<PbacResource> getExactlyByPattern(String pattern) {
    return lambdaQuery(
        .select(Resource::getId, Resource::getPolicyId)
        .eq(Resource::getMatchType, MatchType.EXACTLY.getValue())
        .eq(Resource::getPattern, pattern)
        .oneOpt()
        .map(PbacConvertor.INSTANCE::resource2PbacResource);

  }

  @Override
  public PbacRuleResult getRuleResultByPattern(String pattern) {
    String key = PBAC_PATH_PREFIX + pattern;
    if (!redisTemplate.hasKey(key)) {
      synchronized (key) {
        if (!redisTemplate.hasKey(key)) {
          PbacResource pbacResource = getExactlyByPattern(pattern)
              .or(() -> getFuzzyByPattern(pattern))
              .orElseThrow(() -> new BusiPbacException("未匹配对应的资源"));
          redisTemplate.opsForValue().set(key, pbacResource);
        }
      }
    }
    PbacResource pbacResource = (PbacResource) redisTemplate.opsForValue().get(key);
    return policyRuleService.pbacRuleResultByPolicyId(pbacResource);
  }

  private Optional<PbacResource> getFuzzyByPattern(String pattern) {
    Map<String, PbacResource> entries = getFuzzyPatternMap();
    PbacResource resource = null;
    for (Entry<String, PbacResource> entry : entries.entrySet()) {
      if (ANT_PATH_MATCHER.match(entry.getKey(), pattern)) {
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }

  private Map<String, PbacResource> getFuzzyPatternMap() {
    if (!redisTemplate.hasKey(PBAC_FUZZY_RESOURCE)) {
      synchronized (this) {
        if (!redisTemplate.hasKey(PBAC_FUZZY_RESOURCE)) {
          Map<String, PbacResource> collect = lambdaQuery()
              .select(Resource::getPolicyId, Resource::getPattern,Resource::getId)
              .eq(Resource::getMatchType, MatchType.FUZZY.getValue())
              .list()
              .stream()
              .collect(Collectors.toMap(Resource::getPattern, PbacConvertor.INSTANCE::resource2PbacResource));
          redisTemplate.opsForHash().putAll(PBAC_FUZZY_RESOURCE, collect);
        }
      }
    }
    return redisTemplate.opsForHash().entries(PBAC_FUZZY_RESOURCE);
  }
}
