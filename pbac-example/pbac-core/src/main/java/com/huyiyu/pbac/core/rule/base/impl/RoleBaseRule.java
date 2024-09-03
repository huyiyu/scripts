package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.rule.base.AbstractRuleElement;
import com.huyiyu.pbac.core.domain.PbacContext;

public class RoleBaseRule extends AbstractRuleElement<Object> {


  @Override
  public boolean decide(PbacContext loginUser, Object configuration) {
    // TODO roleBase
    return false;
  }

}
