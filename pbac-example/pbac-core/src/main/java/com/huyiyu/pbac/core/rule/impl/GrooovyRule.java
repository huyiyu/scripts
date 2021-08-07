package com.huyiyu.pbac.core.rule.impl;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.rule.AbstractRuleElement;
import com.huyiyu.pbac.core.rule.PolicyRuleParam;
import java.util.Map;

public class GrooovyRule extends AbstractRuleElement<Map> {

  private PolicyRuleParam policyRuleParam;

  public GrooovyRule(PolicyRuleParam policyRuleParam) {
    this.policyRuleParam = policyRuleParam;
  }

  @Override
  public boolean decide(LoginUser loginUser, Map configuration) {
    return false;
  }

}
