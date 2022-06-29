package com.huyiyu.simple.sample.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.huyiyu.excel.entity.RowResult;
import lombok.Data;
import org.apache.poi.ss.usermodel.Font;

@Data
public class ShortEntityResult implements RowResult {

    @ExcelProperty("行号")
    private int rowIndex;
    @ContentFontStyle(color = Font.COLOR_RED)
    @ExcelProperty("失败原因")
    private String errorMessage;
    @ExcelProperty("展示字段")
    private String field10;
}
