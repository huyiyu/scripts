package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.rule.base.AbstractRuleElement;
import com.huyiyu.pbac.core.rule.base.RuleContext;
import java.util.Map;

public class GrooovyRule extends AbstractRuleElement<Map> {
  public GrooovyRule() {
  }

  @Override
  public boolean decide(RuleContext ruleContext, Map configuration) {
    // TODO groovy
    return false;
  }

}