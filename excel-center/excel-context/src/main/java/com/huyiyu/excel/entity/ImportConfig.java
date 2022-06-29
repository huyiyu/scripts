package com.huyiyu.excel.entity;

public interface ImportConfig {

    Long getId();
    Integer getReadStep();
    Integer getMaxRow();
    String getType();
    Long getLockSecond();
    Long getReleaseSecond();
    Boolean getSync();
    Integer getWriteStep();
}
