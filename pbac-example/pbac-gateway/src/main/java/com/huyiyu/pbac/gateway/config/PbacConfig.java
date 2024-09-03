package com.huyiyu.pbac.gateway.config;


import com.huyiyu.pbac.core.rule.base.AbstractRuleElement;
import com.huyiyu.pbac.core.rule.base.RuleChainFactory;
import com.huyiyu.pbac.gateway.pbac.URIReactiveExecutorPoint;
import com.huyiyu.pbac.gateway.service.impl.SecurityExector;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class PbacConfig {

  @Bean
  public SecurityExector securityExector(URIReactiveExecutorPoint uriReactiveExecutorPoint, RuleChainFactory ruleChainFactory) {
    return new SecurityExector(ruleChainFactory, uriReactiveExecutorPoint);
  }

  @Bean
  public URIReactiveExecutorPoint uriReactiveExecutorPoint(ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate, WebClient webClient) {
    return new URIReactiveExecutorPoint(webClient, reactiveRedisTemplate);
  }

}
