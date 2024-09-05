package com.huyiyu.pbac.core.exception;

import com.huyiyu.pbac.core.domain.PbacRuleResult.PbacPolicyRule;
import java.text.MessageFormat;

public class RuleNotFoundException extends BusiPbacException {

  public RuleNotFoundException(PbacPolicyRule pbacPolicyRule) {

    super(MessageFormat.format("规则名称({0})不存在对应执行对象",pbacPolicyRule.getHandlerName()));
  }

  public RuleNotFoundException(String message) {super(message);}
}
