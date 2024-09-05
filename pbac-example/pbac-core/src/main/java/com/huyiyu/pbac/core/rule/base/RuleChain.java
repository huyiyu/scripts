package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacContext;

public interface RuleChain {

  void executeRule(PbacContext ruleContext);
}
