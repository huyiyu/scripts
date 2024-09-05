package com.huyiyu.pbac.gateway.config;


import com.huyiyu.pbac.core.jwt.JwtService;
import com.huyiyu.pbac.core.rule.base.RuleChainFactory;
import com.huyiyu.pbac.gateway.pbac.rule.GateWayRoleRule;
import com.huyiyu.pbac.gateway.pbac.URIReactiveExecutorPoint;
import com.huyiyu.pbac.gateway.pbac.rule.IdentityRule;
import com.huyiyu.pbac.gateway.service.impl.SecurityExector;
import io.micrometer.core.instrument.Meter.Id;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class PbacConfig {

  @Bean
  public SecurityExector securityExector(URIReactiveExecutorPoint uriReactiveExecutorPoint,
      RuleChainFactory ruleChainFactory,
      JwtService jwtService) {
    return new SecurityExector(uriReactiveExecutorPoint, ruleChainFactory, jwtService);
  }

  @Bean
  public GateWayRoleRule gateWayRoleRule(RestClient restClient,
      RedisTemplate redisTemplate) {
    return new GateWayRoleRule(restClient, redisTemplate);
  }

  @Bean
  public IdentityRule identityRule(RestClient restTemplate, RedisTemplate redisTemplate,
      RestClient restClient) {
    return new IdentityRule(restClient, redisTemplate);
  }

  @Bean
  public URIReactiveExecutorPoint uriReactiveExecutorPoint(
      ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate, WebClient webClient) {
    return new URIReactiveExecutorPoint(webClient, reactiveRedisTemplate);
  }

}
