package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacPolicyRule;
import com.huyiyu.pbac.core.domain.PbacContext;
import java.util.List;
import java.util.Map;

public class SimpleRuleChain implements RuleChain {

  private int pos = 0;
  private Boolean result;
  private List<PbacPolicyRule> policyRuleParams;
  private Map<String, AbstractRuleElement<?>> factory;

  public SimpleRuleChain(List<PbacPolicyRule> policyRuleParams, Map<String, AbstractRuleElement<?>> factory) {
    this.policyRuleParams = policyRuleParams;
    this.factory = factory;
  }


  @Override
  public void next(PbacContext ruleContext) {
    if (pos < policyRuleParams.size()) {
      PbacPolicyRule policyRuleParam = policyRuleParams.get(pos++);
      AbstractRuleElement<?> abstractRuleElement = factory.get(policyRuleParam.getHandlerName());
      abstractRuleElement.next(ruleContext, policyRuleParam, this);
    }
  }

  public void end(boolean result) {
    this.pos = policyRuleParams.size();
    this.result = result;
  }

  @Override
  public boolean finalDesidission(){
    return result;
  }
}
