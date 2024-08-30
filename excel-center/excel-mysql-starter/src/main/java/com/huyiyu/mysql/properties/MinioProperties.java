package com.huyiyu.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String downloadPrefix;
}
