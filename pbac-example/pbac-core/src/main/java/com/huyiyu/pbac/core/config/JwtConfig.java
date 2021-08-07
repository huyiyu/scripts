package com.huyiyu.pbac.core.config;

import com.huyiyu.pbac.core.jwt.impl.NimbusJwtService;
import com.huyiyu.pbac.core.jwt.JwtService;
import com.huyiyu.pbac.core.property.PbacProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PbacProperties.class)
public class JwtConfig {
  @Bean
  public JwtService jwtService(PbacProperties pbacProperties) {
    return new NimbusJwtService(pbacProperties.getJwt());
  }
}
