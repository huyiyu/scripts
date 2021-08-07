package com.huyiyu.pbac.core.rule;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.rule.impl.GrooovyRule;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RuleChain {

  private int pos = 0;
  private Boolean result;
  private LoginUser loginUser;
  private List<PolicyRuleParam> policyRuleParams;
  private Map<String, AbstractRuleElement<?>> factory;

  public RuleChain(List<PolicyRuleParam> policyRuleParams, LoginUser loginUser,
      Map<String, AbstractRuleElement<?>> factory) {
    this.loginUser = loginUser;
    this.policyRuleParams = policyRuleParams;
    this.factory = factory;
  }


  public void next() {
    if (pos < policyRuleParams.size()) {
      PolicyRuleParam policyRuleParam = policyRuleParams.get(pos++);
      AbstractRuleElement<?> abstractRuleElement = factory.get(policyRuleParam.getHandlerName());
      abstractRuleElement.next(loginUser, policyRuleParam, this);
    }
  }

  public void end(boolean result) {
    this.pos = policyRuleParams.size();
    this.result = result;
  }
}
