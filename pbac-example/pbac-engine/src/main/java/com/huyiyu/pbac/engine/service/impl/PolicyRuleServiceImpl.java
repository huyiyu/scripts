package com.huyiyu.pbac.engine.service.impl;

import static com.huyiyu.pbac.core.constant.PbacConstant.PBAC_POLICY_ID_PREFIX;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.core.domain.PbacResource;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.domain.PbacRuleResult.PbacPolicyRule;
import com.huyiyu.pbac.engine.convert.PbacConvertor;
import com.huyiyu.pbac.engine.dto.RuleNameScriptDTO;
import com.huyiyu.pbac.engine.entity.PolicyRule;
import com.huyiyu.pbac.engine.mapper.PolicyRuleMapper;
import com.huyiyu.pbac.engine.service.IPolicyRuleService;
import com.huyiyu.pbac.engine.service.IRuleService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
  private final RedisTemplate redisTemplate;

  @Override
  public PbacRuleResult pbacRuleResultByPolicyId(PbacResource pbacResource) {
    String key = PBAC_POLICY_ID_PREFIX + pbacResource.getPolicyId();
    if (!redisTemplate.hasKey(key)) {
      synchronized (key) {
        if (!redisTemplate.hasKey(key)) {
          PbacRuleResult fromDB = getFromDB(pbacResource);
          redisTemplate.opsForValue().set(key, fromDB);
        }
      }
    }
    return (PbacRuleResult) redisTemplate.opsForValue().get(key);
  }

  private PbacRuleResult getFromDB(PbacResource pbacResource) {
    List<PolicyRule> list = lambdaQuery()
        .select(PolicyRule::getConditionType, PolicyRule::getRuleId, PolicyRule::getParamValue)
        .eq(PolicyRule::getPolicyId, pbacResource.getPolicyId())
        .orderByAsc(PolicyRule::getPirority)
        .list();
    Set<Long> ruleIdList = list.stream()
        .map(PolicyRule::getRuleId)
        .collect(Collectors.toSet());
    Map<Long, RuleNameScriptDTO> mapByRuleIds = ruleService.getHandlerNameAndScriptMapByRuleIds(
        ruleIdList);
    List<PbacPolicyRule> pbacPolicyRules = list.stream()
        .map(policyRule -> PbacConvertor.INSTANCE.policyRuleAndRuleNameScriptDTO2PbacPolicyRule(
            policyRule, mapByRuleIds.get(policyRule.getRuleId())))
        .toList();
    return new PbacRuleResult()
        .setPolicyId(pbacResource.getPolicyId())
        .setResourceId(pbacResource.getId())
        .setPolicyRuleParams(pbacPolicyRules);
  }
}
