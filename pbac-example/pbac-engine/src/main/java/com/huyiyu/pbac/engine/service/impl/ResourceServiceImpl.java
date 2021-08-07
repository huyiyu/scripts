package com.huyiyu.pbac.engine.service.impl;

import com.huyiyu.pbac.engine.entity.Resource;
import com.huyiyu.pbac.engine.mapper.ResourceMapper;
import com.huyiyu.pbac.engine.service.IPolicyDefineService;
import com.huyiyu.pbac.engine.service.IResourcePolicyInstanceService;
import com.huyiyu.pbac.engine.service.IResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.core.enums.MatchType;
import com.huyiyu.pbac.core.policy.PolicyHandler;
import com.huyiyu.pbac.core.policy.PolicyMatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    IResourceService {

  private final IResourcePolicyInstanceService resourcePolicyInstanceService;
  private final IPolicyDefineService policyDefineService;

  private Optional<Long> getExactlyByPattern(String resource) {
    return lambdaQuery()
        .select(Resource::getId)
        .eq(Resource::getMatchType, MatchType.EXACTLY.getValue())
        .eq(Resource::getPattern, resource)
        .oneOpt()
        .map(Resource::getId);
  }

  @Override
  public Optional<Long> getResourceIdByPattern(HttpServletRequest request) {
//    return getExactlyByPattern(request.getRequestURI())
//        .or(() -> getFuzzyByPattern(request));
    return Optional.empty();
  }
}
