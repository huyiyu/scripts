package com.cibidf.pbac.service.impl;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cibidf.pbac.entity.PolicyInstance;
import com.cibidf.pbac.mapper.PolicyInstanceMapper;
import com.cibidf.pbac.service.IPolicyInstanceService;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 策略表,规定了configuration的内容 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
@Service
public class PolicyInstanceServiceImpl extends ServiceImpl<PolicyInstanceMapper, PolicyInstance> implements IPolicyInstanceService {



  @Override
  public List<Pair<Long, String>> listPolicyIdByIds(List<Long> list) {
    return lambdaQuery()
        .select(PolicyInstance::getPolicyDefineId, PolicyInstance::getParamValue)
        .in(PolicyInstance::getId, list)
        .list()
        .stream().map(policyInstance -> Pair.of(policyInstance.getPolicyDefineId(),
            policyInstance.getParamValue()))
        .toList();
  }
}
