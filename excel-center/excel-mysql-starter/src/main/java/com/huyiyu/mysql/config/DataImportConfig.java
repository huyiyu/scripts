package com.huyiyu.mysql.config;
import com.huyiyu.excel.factory.ExcelReadFactory;
import com.huyiyu.excel.service.ImportConfigService;
import com.huyiyu.excel.service.ImportHistoryService;
import com.huyiyu.excel.service.StorageService;
import com.huyiyu.mysql.properties.MinioProperties;
import com.huyiyu.mysql.service.impl.ExcelImportConfigServiceImpl;
import com.huyiyu.mysql.service.impl.ExcelImportHistoryServiceImpl;
import com.huyiyu.mysql.service.impl.MinioStorageServiceImpl;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class DataImportConfig {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties){
        return MinioClient.builder()
            .endpoint(minioProperties.getEndpoint())
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();
    }

    @Bean
    public StorageService storageService(MinioProperties minioProperties,MinioClient minioClient) {
        MinioStorageServiceImpl minioStorageService = new MinioStorageServiceImpl();
        minioStorageService.setMinioClient(minioClient);
        minioStorageService.setMinioProperties(minioProperties);
        return minioStorageService;
    }

    @Bean
    public ImportHistoryService importHistoryService() {
        return new ExcelImportHistoryServiceImpl();
    }

    @Bean
    public ImportConfigService importConfigService(RedisTemplate redisTemplate) {
        ExcelImportConfigServiceImpl excelImportConfigService = new ExcelImportConfigServiceImpl();
        excelImportConfigService.setRedisTemplate(redisTemplate);
        return excelImportConfigService;
    }

    @Bean
    public ExcelReadFactory excelReadFactory(
        StorageService storageService,
        ImportHistoryService importHistoryService,
        RedisTemplate redisTemplate
        ) {
        ExcelReadFactory excelReadFactory = new ExcelReadFactory();
        excelReadFactory.setStorageService(storageService);
        excelReadFactory.setImportHistoryService(importHistoryService);
        excelReadFactory.setRedisTemplate(redisTemplate);
        return excelReadFactory;
    }
}
