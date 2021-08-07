package com.huyiyu.mysql.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateStringConvertor implements Converter<LocalDate> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(
        ReadCellData<?> cellData, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration) throws ParseException {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return LocalDate.parse(cellData.getStringValue());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat());
            return LocalDate.parse(cellData.getStringValue(), formatter);
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDate value, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new WriteCellData<>(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            return new WriteCellData<>(
                value.format(DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat())));
        }
    }

}
