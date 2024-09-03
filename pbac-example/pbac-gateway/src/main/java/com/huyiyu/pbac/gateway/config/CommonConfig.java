package com.huyiyu.pbac.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class CommonConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .build();
  }

}
