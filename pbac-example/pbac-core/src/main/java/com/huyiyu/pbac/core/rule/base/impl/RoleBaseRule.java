package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.rule.base.AbstractRuleElement;
import com.huyiyu.pbac.core.rule.base.RuleContext;

public class RoleBaseRule extends AbstractRuleElement<Object> {


  @Override
  public boolean decide(RuleContext loginUser, Object configuration) {
    // TODO roleBase
    return false;
  }

}
