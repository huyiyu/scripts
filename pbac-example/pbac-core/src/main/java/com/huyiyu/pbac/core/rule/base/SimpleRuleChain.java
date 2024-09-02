package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.domain.PolicyRuleParam;
import java.util.List;
import java.util.Map;

public class SimpleRuleChain implements RuleChain {

  private int pos = 0;
  private Boolean result;
  private List<PolicyRuleParam> policyRuleParams;
  private Map<String, AbstractRuleElement<?>> factory;

  public SimpleRuleChain(List<PolicyRuleParam> policyRuleParams, Map<String, AbstractRuleElement<?>> factory) {
    this.policyRuleParams = policyRuleParams;
    this.factory = factory;
  }


  @Override
  public void next(RuleContext ruleContext) {
    if (pos < policyRuleParams.size()) {
      PolicyRuleParam policyRuleParam = policyRuleParams.get(pos++);
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
