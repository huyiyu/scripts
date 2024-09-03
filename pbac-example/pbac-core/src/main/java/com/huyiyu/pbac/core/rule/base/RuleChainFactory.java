package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.domain.PbacPolicyRule;
import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.rule.reactive.ReactiveExecutorPoint;
import com.huyiyu.pbac.core.rule.simply.ExecutorPoint;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RuleChainFactory {

  private final Map<String, AbstractRuleElement<?>> ruleElementMap;

  private RuleChain createRuleChain(List<PbacPolicyRule> patternTransfer) {
    return new SimpleRuleChain(patternTransfer, ruleElementMap);
  }



  private <T> boolean executor(PbacContext ruleContext) {
    RuleChain ruleChain = createRuleChain(ruleContext.getPolicyRuleParamList());
    ruleChain.next(ruleContext);
    return ruleChain.finalDesidission();
  }

  public <T> boolean decide(ExecutorPoint<T> executorPoint, T t, PbacUser loginUser) {
    PbacContext ruleContext = executorPoint.getPolicyRuleParam(t, loginUser);
    return executor(ruleContext);
  }

  public <T> Mono<Boolean> decide(ReactiveExecutorPoint<T> executorPoint,T t, PbacUser loginUser) {
    return executorPoint.getPolicyRuleParam(t,loginUser)
        .map(this::executor);
  }
}
