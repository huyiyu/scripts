package com.huyiyu.deploy.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("deploy")
public class DeployProperties {

  private Repo repo;
  private Flyway flyway;

  @Data
  public static class Repo {

    /**
     * 仓库地址
     */
    private String url;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 工作目录
     */
    private String workdir = System.getProperty("user.dir");

  }

  @Data
  public static class Flyway {
    private String baselineVersion = "1.0.0";
    private boolean baselineOnMigrate = true;
    private String mirgatePrefix = "V";
    private String migrateSuffix = ".sql";
    private String migrateSeparator = "_";
    private String schema = "flywaydb";
    private String locations = "classpath:db/migration";
    private String jdbcUrl;
    private String username;
    private String password;

  }
}
