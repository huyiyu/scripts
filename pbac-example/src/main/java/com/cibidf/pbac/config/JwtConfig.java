package com.cibidf.pbac.config;

import com.cibidf.pbac.auth.Sm3PasswordEncoder;
import com.cibidf.pbac.auth.jwt.JwtService;
import com.cibidf.pbac.auth.jwt.NimbusJwtService;
import com.cibidf.pbac.property.PbacProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
public class JwtConfig {
  @Bean
  public JwtService jwtService(PbacProperties pbacProperties) {
    return new NimbusJwtService(pbacProperties.getJwt());
  }
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new Sm3PasswordEncoder();
  }
}
