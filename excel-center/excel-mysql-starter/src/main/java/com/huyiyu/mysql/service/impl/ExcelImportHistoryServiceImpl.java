package com.huyiyu.mysql.service.impl;

import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.entity.ImportHistory;
import com.huyiyu.excel.service.ImportHistoryService;
import com.huyiyu.mysql.entity.ExcelImportConfig;
import com.huyiyu.mysql.entity.ExcelImportHistory;
import com.huyiyu.mysql.mapper.ExcelImportHistoryMapper;
import com.huyiyu.mysql.service.IExcelImportHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据导入历史 服务实现类
 * </p>
 *
 * @author zyy
 * @since 2022-06-29
 */
public class ExcelImportHistoryServiceImpl extends ServiceImpl<ExcelImportHistoryMapper, ExcelImportHistory> implements IExcelImportHistoryService, ImportHistoryService {

    @Override
    public ImportHistory newImportHistory(ImportConfig config) {
        ExcelImportConfig config1 = (ExcelImportConfig) config;
        ExcelImportHistory excelImportHistory = new ExcelImportHistory();
        excelImportHistory.setProject(config1.getProject());
        excelImportHistory.setImportConfigId(config1.getId());
        excelImportHistory.setType(config1.getType());
        excelImportHistory.setStartTime(LocalDateTime.now());
        save(excelImportHistory);
        return excelImportHistory;
    }

    @Override
    public boolean updateById(ImportHistory importHistory) {
        ExcelImportHistory excelImportHistory = ((ExcelImportHistory) importHistory);
        return updateById(excelImportHistory);
    }

    @Override
    public boolean updateExceptionById(String errorMsg, Long id) {
        return lambdaUpdate()
            .set(ExcelImportHistory::getErrorMsg, errorMsg)
            .eq(ExcelImportHistory::getId, id)
            .update();
    }
}
