package com.huyiyu.excel.factory;

import com.alibaba.excel.EasyExcel;
import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.entity.ImportHistory;
import com.huyiyu.excel.entity.RowResult;
import com.huyiyu.excel.listener.ReadListenerAdapter;
import com.huyiyu.excel.service.ImportHistoryService;
import com.huyiyu.excel.service.StorageService;
import com.huyiyu.excel.util.ExceptionUtil;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Setter
public class ExcelReadFactory {

    private StorageService storageService;

    private ImportHistoryService importHistoryService;

    private RedisTemplate redisTemplate;

    @Transactional
    public <T> ReadListenerAdapter<T> doRead(InputStream inputStream, Class<T> clazz,
        Function<Map<Integer, T>, List<? extends RowResult>> callback,
        ImportConfig config, ImportHistory importHistory) {
        ReadListenerAdapter<T> readListenerAdapter = null;
        try {
            readListenerAdapter = new ReadListenerAdapter<>(config,
                importHistoryService, storageService, callback, importHistory, redisTemplate);
            EasyExcel.read(inputStream, clazz, readListenerAdapter).sheet().doRead();
        } catch (Exception e) {
            //此处抛出异常也不会有打印,直接log即可,该线程归还给线程池
            importHistoryService
                .updateExceptionById(ExceptionUtil.toString(e), importHistory.getId());
            log.error("type:<{}>HistoryId<{}>导入时发生异常", config.getType(), importHistory.getId(), e);
            throw new RuntimeException(e);
        }
        return readListenerAdapter;
    }
}
