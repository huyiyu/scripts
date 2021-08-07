package com.huyiyu.pbac.core.rule.impl;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.rule.AbstractRuleElement;
import com.huyiyu.pbac.core.rule.AbstractRuleElement.NoneParam;
import com.huyiyu.pbac.core.rule.PolicyRuleParam;

public class RoleBaseRule extends AbstractRuleElement<NoneParam> {

  protected RoleBaseRule(PolicyRuleParam policyRuleParam) {
    super(policyRuleParam);
  }

  @Override
  public boolean decide(LoginUser loginUser, NoneParam configuration) {
    return false;
  }

}
