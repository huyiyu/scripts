package com.huyiyu.pbac.gateway.config;

import static com.alibaba.nacos.client.utils.ParamUtil.setConnectTimeout;

import java.time.Duration;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class CommonConfig {

  @Bean
  public WebClient webClient(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
    return WebClient.builder()
        .filter(lbFunction)
        .build();
  }

  @Bean
  public RestClient restClient(ClientHttpRequestInterceptor loadBalancerInterceptor) {
    return RestClient.builder()
        .requestInterceptor(loadBalancerInterceptor)
        .build();
  }

}
