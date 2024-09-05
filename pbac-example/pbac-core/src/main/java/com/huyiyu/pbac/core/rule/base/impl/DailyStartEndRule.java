package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.rule.base.AbstractGenericRule;
import com.huyiyu.pbac.core.rule.base.impl.DailyStartEndRule.DailyParam;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Data;

public class DailyStartEndRule extends AbstractGenericRule<DailyParam> {

  @Override
  public boolean decideWithType(PbacContext ruleContext, DailyParam configuration) {
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
