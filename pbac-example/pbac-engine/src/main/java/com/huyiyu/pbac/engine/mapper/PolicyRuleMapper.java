package com.huyiyu.pbac.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huyiyu.pbac.core.domain.PbacRuleResult.PbacPolicyRule;
import com.huyiyu.pbac.engine.entity.PolicyRule;
import java.util.List;

/**
 * <p>
 * 规则组合表 Mapper 接口
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
public interface PolicyRuleMapper extends BaseMapper<PolicyRule> {

  List<PbacPolicyRule> getPbacPolicyRuleByPolicyId(Long policyId);
}
