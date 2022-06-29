package com.huyiyu.excel.service;


import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.entity.ImportHistory;
import com.huyiyu.excel.entity.RowResult;
import com.huyiyu.excel.factory.ExcelReadFactory;
import com.huyiyu.excel.listener.ReadListenerAdapter;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class AbstractImportService<T> {

    @Resource
    private ExcelReadFactory excelReadFactory;
    @Resource
    private ImportConfigService importConfigService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private final Class clazz;
    @Resource
    private RedissonClient redissonClient;

    private static final String DATA_IMPORT_LOCK_PREFIX = "DATA_IMPORT_LOCK_PREFIX:";
    @Resource
    private ImportHistoryService importHistoryService;

    /**
     * 获取当前 class 泛形
     */
    public AbstractImportService() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            clazz = (Class) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        } else {
            this.clazz = Object.class;
        }
    }

    public Long doRead(InputStream inputStream) {
        String simpleName = clazz.getSimpleName();
        ImportConfig config = importConfigService.getConfig(simpleName);
        ImportHistory importHistory = importHistoryService.newImportHistory(config);
        if (log.isDebugEnabled()) {
            log.debug("当前类型:<{}>,使用<{}>的方法解析Excel", simpleName, config.getSync() ? "同步" : "异步");
        }
        if (config.getSync()) {
            read(simpleName, config, inputStream,importHistory);
        } else {
            threadPoolTaskExecutor.submit(() -> read(simpleName, config, inputStream, importHistory));
        }
        return importHistory.getId();
    }



    private ReadListenerAdapter<T> read(String simpleName, ImportConfig config,
        InputStream inputStream, ImportHistory importHistory) {
        String lockName = DATA_IMPORT_LOCK_PREFIX + simpleName;
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean getLock = lock
                .tryLock(config.getLockSecond(), config.getReleaseSecond(), TimeUnit.SECONDS);
            if (getLock) {
                return excelReadFactory.doRead(inputStream, clazz, this::invoke, config, importHistory);
            } else {
                log.warn("lock:<{}>在<{}>秒内未获取到分布式锁,请调长等待时间", lockName, config.getLockSecond());
                return null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 注意,该方法是分步完成的,具体会根据step 分步 如果需要考虑去重, 可使用ThreadLocal共享数据 记得remove,去重的行请返回错误信息。
     * 所有导入在一个事务里面完成,如果需要回滚事务,请在onException抛出异常
     *
     * @param data 导入数据封装,key 是具体行号,value 是具体值
     * @return 错误的行
     */
    public abstract List<? extends RowResult> invoke(Map<Integer, T> data);

}
