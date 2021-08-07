package com.huyiyu.simple.service.impl;


import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.entity.ImportHistory;
import com.huyiyu.excel.service.ImportHistoryService;
import com.huyiyu.simple.entity.SimpleImportHistory;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleLogHistoryService implements ImportHistoryService {

    @Override
    public ImportHistory newImportHistory(ImportConfig config) {
        ImportHistory importHistory = new SimpleImportHistory();
        importHistory.setImportConfigId(config.getId());
        importHistory.setType(config.getType());
        importHistory.setStartTime(LocalDateTime.now());
        log.info("新的导入历史:{}",importHistory);
        return importHistory;
    }

    @Override
    public boolean updateById(ImportHistory importHistory) {
        log.info(importHistory.toString());
        return true;
    }

    @Override
    public boolean updateExceptionById(String toString, Long id) {
        return false;
    }
}
