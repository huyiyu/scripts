package com.cibidf.pbac.service.impl;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cibidf.pbac.entity.ResourcePolicyInstance;
import com.cibidf.pbac.mapper.ResourcePolicyInstanceMapper;
import com.cibidf.pbac.service.IPolicyInstanceService;
import com.cibidf.pbac.service.IResourcePolicyInstanceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 规则资源关联表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
@Service
@RequiredArgsConstructor
public class ResourcePolicyInstanceServiceImpl extends ServiceImpl<ResourcePolicyInstanceMapper, ResourcePolicyInstance> implements IResourcePolicyInstanceService {

  private final IPolicyInstanceService policyInstanceService;

  @Override
  public List<Pair<Long, String>> listPairByResourceId(Long resId) {
    List<Long> list = listPolicyInstanceIdByResourceId(resId);
    return policyInstanceService.listPolicyIdByIds(list);
  }

  private List<Long> listPolicyInstanceIdByResourceId(Long resId) {
    return lambdaQuery()
        .select(ResourcePolicyInstance::getPolicyInstanceId)
        .eq(ResourcePolicyInstance::getResourceId, resId)
        .list().stream()
        .map(ResourcePolicyInstance::getPolicyInstanceId)
        .toList();
  }
}
