package com.huyiyu.pbac.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConditionType {

  AND(1, "必要"),
  OR(2, "充分");
  private int value;
  private String desc;


  private static final Map<Integer, ConditionType> META_INFO = Arrays
      .stream(values())
      .collect(Collectors.toMap(ConditionType::getValue, Function.identity()));


  public static ConditionType of(int value) {
    return META_INFO.get(value);
  }
}
