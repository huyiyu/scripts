package com.huyiyu.excel.service;


import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.entity.ImportHistory;

public interface ImportHistoryService {

    ImportHistory newImportHistory(ImportConfig config);
    boolean updateById(ImportHistory importHistory);
    boolean updateExceptionById(String toString, Long id);
}
