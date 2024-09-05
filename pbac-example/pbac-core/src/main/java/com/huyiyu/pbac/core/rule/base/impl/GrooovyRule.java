package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.rule.base.AbstractGenericRule;
import com.huyiyu.pbac.core.domain.PbacContext;
import java.util.Map;

public class GrooovyRule extends AbstractGenericRule<Map> {

  private final String script;

  public GrooovyRule(String script) {
    this.script = script;
  }

  @Override
  public boolean decideWithType(PbacContext ruleContext, Map configuration) {
    // TODO groovy
    return false;
  }

}
