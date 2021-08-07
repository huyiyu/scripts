package com.huyiyu.pbac.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.huyiyu.pbac.biz.mapper")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
