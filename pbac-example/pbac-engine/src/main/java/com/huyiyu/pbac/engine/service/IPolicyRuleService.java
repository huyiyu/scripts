package com.huyiyu.pbac.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.core.domain.PbacPolicyRule;
import com.huyiyu.pbac.engine.entity.PolicyRule;
import java.util.List;

/**
 * <p>
 * 规则组合表 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
public interface IPolicyRuleService extends IService<PolicyRule> {

  List<PbacPolicyRule> listPbacPolicyRuleByPolicyId(Long policyId);
}
