package com.huyiyu.excel.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.read.listener.ReadListener;
import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.entity.ImportHistory;
import com.huyiyu.excel.entity.RowResult;
import com.huyiyu.excel.list.RedisReadOnlyList;
import com.huyiyu.excel.service.ImportHistoryService;
import com.huyiyu.excel.service.StorageService;
import com.huyiyu.excel.util.ExceptionUtil;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StopWatch;

@Slf4j
public class ReadListenerAdapter<T> implements ReadListener<T> {

    private static final String EXCEL_RESULT_PREFIX = "EXCEL_RESULT:";

    private final ImportConfig importConfig;
    private final ImportHistoryService importHistoryService;
    private final StorageService storageService;
    private final Function<Map<Integer, T>, List<? extends RowResult>> callback;
    private final Map<Integer, T> cachedData = new TreeMap<>();
    private final RedisTemplate redisTemplate;
    private final String RESULT_KEY;
    @Getter
    private volatile int currentRow;
    @Getter
    private volatile int endRow;
    @Getter
    private int totalRow;
    private ImportHistory importHistory;
    private StopWatch stopWatch;


    public ReadListenerAdapter(ImportConfig importConfig,
        ImportHistoryService importHistoryService, StorageService storageService,
        Function<Map<Integer, T>, List<? extends RowResult>> callback,
        ImportHistory importHistory, RedisTemplate redisTemplate) {
        this.importConfig = importConfig;
        this.importHistoryService = importHistoryService;
        this.storageService = storageService;
        this.callback = callback;
        this.importHistory = importHistory;
        this.redisTemplate = redisTemplate;
        this.RESULT_KEY = EXCEL_RESULT_PREFIX + importConfig.getType() + ":" + importHistory.getId();
        stopWatch = new StopWatch(importConfig.getType());
        stopWatch.start();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("historyId:<{}> 导入发生异常,请查询History记录", importHistory.getId());
        importHistory.setErrorMsg(ExceptionUtil.toString(exception));
        importHistory.setSuccess(false);
        if (stopWatch.isRunning()) {
            stopWatch.stop();
            importHistory.setTimeMillis(stopWatch.getTotalTimeMillis());
        }
        importHistoryService.updateById(importHistory);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        if (log.isDebugEnabled()) {
            log.debug("解析到一条数据:{}", data);
        }
        cachedData.put(context.readRowHolder().getRowIndex(), data);
        int step = importConfig.getReadStep();
        this.currentRow = context.readRowHolder().getRowIndex();
        if (step > 0 && cachedData.size() >= step) {
            totalRow += cachedData.size();
            endRow = currentRow + cachedData.size();
            clearAndCollectResult();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cachedData.isEmpty()) {
            totalRow += cachedData.size();
            this.endRow = currentRow + cachedData.size();
            clearAndCollectResult();
        }
        writeImportHistory(context);
    }

    private void writeImportHistory(AnalysisContext context) {
        RedisReadOnlyList<RowResult> rowResults = new RedisReadOnlyList<>(RESULT_KEY, redisTemplate.opsForList(),importConfig.getWriteStep());
        if (!rowResults.isEmpty()) {
            RowResult rowResult = (RowResult) redisTemplate.opsForList().index(RESULT_KEY, 0);
            Class<?> head = rowResult.getClass();
            String url = storageService.upload(outputStream -> {
                EasyExcel.write(outputStream, head).sheet("result").doWrite(rowResults);
            });
            importHistory.setFileUrl(url);
            redisTemplate.delete(RESULT_KEY);
        }
        importHistory.setSuccess(true);
        importHistory.setTotalRow(totalRow);
        importHistory.setSuccessRow(totalRow - rowResults.size());
        importHistory.setFailureRow(rowResults.size());
        stopWatch.stop();
        importHistory.setTimeMillis(stopWatch.getTotalTimeMillis());
        importHistoryService.updateById(importHistory);
    }

    private void clearAndCollectResult() {
        if (importConfig.getMaxRow() < totalRow) {
            throw new ExcelAnalysisException(
                "类型:<" + importConfig.getType() + ">,总条数:<" + totalRow + ">超过最大限制条数:<"
                    + importConfig.getMaxRow() + ">");
        }
        List<? extends RowResult> errorResult = callback.apply(cachedData);
        redisTemplate.opsForList().rightPushAll(RESULT_KEY, errorResult);
        cachedData.clear();
    }
}
