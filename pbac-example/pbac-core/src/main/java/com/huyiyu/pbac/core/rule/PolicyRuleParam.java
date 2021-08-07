package com.huyiyu.pbac.core.rule;

import com.huyiyu.pbac.core.enums.ConditionType;
import lombok.Data;

@Data
public class PolicyRuleParam {

  private String handlerName;
  private String script;
  private String value;
  private ConditionType conditionType;
}
