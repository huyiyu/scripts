package com.huyiyu.pbac.core.rule.base;

public interface RuleChain {

  void next(RuleContext ruleContext);

  boolean finalDesidission();
}
