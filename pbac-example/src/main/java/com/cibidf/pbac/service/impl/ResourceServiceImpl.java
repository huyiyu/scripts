package com.cibidf.pbac.service.impl;

import cn.hutool.core.lang.Pair;
import com.cibidf.pbac.entity.PolicyDefine;
import com.cibidf.pbac.entity.Resource;
import com.cibidf.pbac.enums.MatchType;
import com.cibidf.pbac.mapper.ResourceMapper;
import com.cibidf.pbac.service.IPolicyDefineService;
import com.cibidf.pbac.service.IResourcePolicyInstanceService;
import com.cibidf.pbac.service.IResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cibidf.pbac.auth.policy.GroovyPolicyHandler;
import com.huyiyu.auth.service.PolicyHandler;
import com.huyiyu.auth.service.PolicyMatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 请求客户端资源 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-03
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements
    IResourceService,
    PolicyMatcher<HttpServletRequest, Long> {

  private final IResourcePolicyInstanceService resourcePolicyInstanceService;
  private final IPolicyDefineService policyDefineService;
  private final Map<String, PolicyHandler> policyHandlers;

  private Optional<Long> getExactlyByPattern(String resource) {
    return lambdaQuery()
        .select(Resource::getId)
        .eq(Resource::getMatchType, MatchType.EXACTLY.getValue())
        .eq(Resource::getPattern, resource)
        .oneOpt()
        .map(Resource::getId);
  }

  private Optional<Long> getFuzzyByPattern(HttpServletRequest httpServletRequest) {
    Map<AntPathRequestMatcher, Long> fuzzyPatternMap = createFuzzyPatternMap();
    for (Entry<AntPathRequestMatcher, Long> entry : fuzzyPatternMap.entrySet()) {
      if (entry.getKey().matches(httpServletRequest)) {
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }

  private Map<AntPathRequestMatcher, Long> createFuzzyPatternMap() {
    return lambdaQuery()
        .select(Resource::getId, Resource::getPattern)
        .eq(Resource::getMatchType, MatchType.FUZZY.getValue())
        .list()
        .stream()
        .collect(Collectors.toMap(resource -> new AntPathRequestMatcher(resource.getPattern()),
            Resource::getId));
  }


  @Override
  public Optional<Long> getResourceIdByPattern(HttpServletRequest request) {
    return getExactlyByPattern(request.getRequestURI())
        .or(() -> getFuzzyByPattern(request));
  }


  @Override
  public List<Pair<Long, String>> match(HttpServletRequest httpServletRequest) {
    return getResourceIdByPattern(httpServletRequest)
        .map(resourcePolicyInstanceService::listPairByResourceId)
        .orElse(new ArrayList<>());
  }

  @Override
  public PolicyHandler getPolicyHandler(Long policyId) {
    PolicyDefine policy = policyDefineService.getById(policyId);
    if (StringUtils.hasText(policy.getHandlerName()) && policyHandlers.containsKey(
        policy.getHandlerName())) {
      return policyHandlers.get(policy.getHandlerName());
    } else {
      return new GroovyPolicyHandler(policy.getScripts());
    }
  }
}
