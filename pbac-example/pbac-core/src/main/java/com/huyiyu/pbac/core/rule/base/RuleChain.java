package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacContext;

public interface RuleChain {

  void next(PbacContext ruleContext);

  boolean finalDesidission();
}
