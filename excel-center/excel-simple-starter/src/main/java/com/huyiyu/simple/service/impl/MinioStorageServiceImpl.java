package com.huyiyu.simple.service.impl;

import com.huyiyu.excel.service.StorageService;
import com.huyiyu.simple.properties.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class MinioStorageServiceImpl implements StorageService {

    private MinioClient minioClient;

    private MinioProperties minioProperties;

    @Override
    public String upload(Consumer<OutputStream> oconsumer) {
        ByteArrayOutputStream pos = new ByteArrayOutputStream();
        oconsumer.accept(pos);
        byte[] bytes = pos.toByteArray();
        return doMinioUpload(new ByteArrayInputStream(bytes));
    }

    private String doMinioUpload(InputStream inputStream) {
        try {
            String filename = UUID.randomUUID() + ".xlsx";
            minioClient.putObject(PutObjectArgs.builder()
                .bucket("excel")
                .object(filename)
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .stream(inputStream, inputStream.available(), -1)
                .build());
            String url = minioProperties.getDownloadPrefix() + Base64.getEncoder().encodeToString(filename.getBytes(StandardCharsets.UTF_8));
            log.info("文件上传成功:url为:{}", url);
            return url;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
