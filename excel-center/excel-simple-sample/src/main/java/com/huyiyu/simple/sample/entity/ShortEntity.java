package com.huyiyu.simple.sample.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.huyiyu.simple.sample.convertor.LocalDateStringConvertor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ShortEntity {
    private String field1;
    @DateTimeFormat("yyyy-MM-dd")
    @ExcelProperty(converter = LocalDateStringConvertor.class)
    private LocalDate field2;
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime field3;
    private String field4;
    private String field5;
    private String field6;
    private String field7;
    private String field8;
    private String field9;
    private String field10;
    private String field11;
    private String field12;
    private String field13;
    private String field14;
    private String field15;
    private String field16;
    private String field17;
    private String field18;
    private String field19;
    private String field20;

}
