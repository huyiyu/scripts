package com.huyiyu.pbac.engine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.core.domain.PbacPolicyRule;
import com.huyiyu.pbac.engine.convert.PbacConvertor;
import com.huyiyu.pbac.engine.dto.RuleNameScriptDTO;
import com.huyiyu.pbac.engine.entity.PolicyRule;
import com.huyiyu.pbac.engine.mapper.PolicyRuleMapper;
import com.huyiyu.pbac.engine.service.IPolicyRuleService;
import com.huyiyu.pbac.engine.service.IRuleService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 规则组合表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
@Service
@RequiredArgsConstructor
public class PolicyRuleServiceImpl extends ServiceImpl<PolicyRuleMapper, PolicyRule> implements
    IPolicyRuleService {

  private final IRuleService ruleService;


  @Override
  public List<PbacPolicyRule> listPbacPolicyRuleByPolicyId(Long policyId) {
    List<PolicyRule> list = lambdaQuery()
        .select(PolicyRule::getConditionType, PolicyRule::getRuleId, PolicyRule::getParamValue)
        .eq(PolicyRule::getPolicyId, policyId)
        .orderByAsc(PolicyRule::getPirority)
        .list();

    List<Long> ruleList = list.stream()
        .map(PolicyRule::getRuleId)
        .distinct()
        .toList();
    return list
        .stream().map(policyRule -> policyRule2PbacPolicyRule(policyRule, ruleList))
        .toList();
  }

  private PbacPolicyRule policyRule2PbacPolicyRule(PolicyRule policyRule, List<Long> ruleList) {
    Map<Long, RuleNameScriptDTO> ruleNameScriptDTO = ruleService.getHandlerNameAndScriptMapByRuleIds(ruleList);
    return PbacConvertor.INSTANCE.policyRuleAndRuleNameScriptDTO2PbacPolicyRule(policyRule,ruleNameScriptDTO.get(policyRule.getRuleId()));
  }
}
