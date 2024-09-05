package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.domain.PbacRuleResult.PbacPolicyRule;
import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.rule.reactive.ReactiveExecutorPoint;
import com.huyiyu.pbac.core.rule.simply.ExecutorPoint;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RuleChainFactory {

  private final Map<String, IPbacRule> ruleElementMap;

  private RuleChain createRuleChain(List<PbacPolicyRule> patternTransfer) {
    return new SimpleRuleChain(patternTransfer, ruleElementMap);
  }


  private <T> boolean executor(PbacRuleResult pbacRuleResult, Object pattern, PbacUser pbacUser) {
    RuleChain ruleChain = createRuleChain(pbacRuleResult.getPolicyRuleParams());
    PbacContext pbacContext = PbacContext.builder()
        .pattern(pattern)
        .pbacUser(pbacUser)
        .resourceId(pbacRuleResult.getResourceId())
        .policyId(pbacRuleResult.getPolicyId())
        .result(false)
        .build();
    ruleChain.executeRule(pbacContext);
    return pbacContext.getResult();
  }

  public <T> boolean decide(ExecutorPoint<T> executorPoint, T t, PbacUser loginUser) {
    PbacRuleResult pbacRuleResult = executorPoint.getPolicyRuleParam(t, loginUser);
    return executor(pbacRuleResult,t,loginUser);
  }

  public <T> Mono<Boolean> decide(ReactiveExecutorPoint<T> executorPoint, T pattern,
      PbacUser pbacUser) {
    return executorPoint.getPolicyRuleParam(pattern, pbacUser)
        .map(pbacRuleResult -> executor(pbacRuleResult, pattern, pbacUser));

  }
}
