package com.huyiyu.simple.enums;

import com.huyiyu.excel.entity.ImportConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SimpleImportConfig implements ImportConfig {

    SHORT(1L, 1000, 1000, "测试小导入", "ShortEntity", "jrjg", 100000, false, 60L, 200L),
    LONG(2L, 1000, 1000, "测试大导入", "LongEntity", "gov", 100000, true, 60L, 200L),
    ASYNC_SORT(3L, 10, 1000, "测试异步小导入", "AsyncShortEntity", "jrjg", 100, false, 60L, 200L),
    ASYNC_LONG(4L, 1000, 1000, "测试异步大导入", "AsyncLongEntity", "gov", 100000, false, 60L, 200L);
    private Long id;
    private Integer readStep;
    private Integer writeStep;
    private String title;
    private String type;
    private String project;
    private Integer maxRow;
    private Boolean sync;
    private Long lockSecond;
    private Long releaseSecond;
}
