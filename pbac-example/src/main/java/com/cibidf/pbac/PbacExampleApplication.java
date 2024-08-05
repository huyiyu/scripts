package com.cibidf.pbac;

import com.cibidf.pbac.property.PbacProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PbacProperties.class)
@MapperScan("com.cibidf.pbac.mapper")
public class PbacExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(PbacExampleApplication.class, args);
  }

}
