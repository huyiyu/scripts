package com.huyiyu.pbac.core.domain;

import com.huyiyu.pbac.core.enums.ConditionType;
import com.huyiyu.pbac.core.rule.base.IPbacRule;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PbacContext {

  private final Object pattern;
  private final Long resourceId;
  private final Long policyId;
  private final String resourceName;
  private final PbacUser pbacUser;
  @Setter
  private IPbacRule currentRule;
  @Setter
  private ConditionType currentRuleConditionType;
  @Setter
  private Boolean result;
}

