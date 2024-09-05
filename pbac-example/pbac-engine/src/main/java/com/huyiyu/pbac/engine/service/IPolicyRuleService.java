package com.huyiyu.pbac.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.core.domain.PbacResource;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.engine.entity.PolicyRule;

/**
 * <p>
 * 规则组合表 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
public interface IPolicyRuleService extends IService<PolicyRule> {

  PbacRuleResult pbacRuleResultByPolicyId(PbacResource pbacResource);
}
