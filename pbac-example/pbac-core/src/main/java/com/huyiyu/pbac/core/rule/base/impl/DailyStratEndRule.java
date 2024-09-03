package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.rule.base.AbstractRuleElement;
import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.rule.base.impl.DailyStratEndRule.DailyParam;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Data;

public class DailyStratEndRule extends AbstractRuleElement<DailyParam> {


  @Override
  public boolean decide(PbacContext ruleContext, DailyParam configuration) {

    LocalTime now = LocalTime.now();
    return  Objects.nonNull(configuration)
        && now.isAfter(configuration.getStartTime())
        && now.isBefore(configuration.getEndTime());
  }

  @Data
  public static final class DailyParam {
    private LocalTime startTime;
    private LocalTime endTime;
  }
}
