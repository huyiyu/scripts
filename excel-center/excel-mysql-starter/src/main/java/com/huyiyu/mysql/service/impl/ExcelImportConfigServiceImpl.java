package com.huyiyu.mysql.service.impl;

import static org.springframework.util.Assert.notNull;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.service.ImportConfigService;
import com.huyiyu.mysql.entity.ExcelImportConfig;
import com.huyiyu.mysql.mapper.ExcelImportConfigMapper;
import com.huyiyu.mysql.service.IExcelImportConfigService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.Setter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据导入类型 服务实现类
 * </p>
 *
 * @author zyy
 * @since 2022-06-29
 */
public class ExcelImportConfigServiceImpl extends
    ServiceImpl<ExcelImportConfigMapper, ExcelImportConfig> implements IExcelImportConfigService,
    ImportConfigService {

    private static final String IMPORT_CONFIG_HASH_KEY = "IMPORT_CONFIG";
    @Setter
    private RedisTemplate redisTemplate;

    @Override
    public ImportConfig getConfig(String simpleName) {
        HashOperations<String, String, ImportConfig> hashOperations = redisTemplate.<String, ImportConfig>opsForHash();
        if (!redisTemplate.hasKey(IMPORT_CONFIG_HASH_KEY)) {
            Map<String, ExcelImportConfig> importConfigMapFromDb = getImportConfigMapFromDb();
            hashOperations.putAll(IMPORT_CONFIG_HASH_KEY,importConfigMapFromDb);
            redisTemplate.expire(IMPORT_CONFIG_HASH_KEY, Duration.ofDays(1));
        }
        ImportConfig importConfig = hashOperations.get(IMPORT_CONFIG_HASH_KEY, simpleName);
        notNull(importConfig,"请配置importConfig");
        return importConfig;
    }

    public Map<String, ExcelImportConfig> getImportConfigMapFromDb() {
        return list().stream()
            .collect(Collectors.toMap(ExcelImportConfig::getType, Function.identity()));
    }


}
