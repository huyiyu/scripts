package com.huyiyu.pbac.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConditionType {

  AND(1, "必要"),
  OR(2, "充分");
  private int value;
  private String desc;

}
