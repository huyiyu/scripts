package com.huyiyu.pbac.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MatchType {
  EXACTLY(1, "精确匹配"),
  FUZZY(2, "模糊匹配");

  private final int value;
  private final String name;
}
