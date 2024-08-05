package com.cibidf.pbac.config;

import com.cibidf.pbac.auth.manage.uri.UriPolicyAuthorizationManager;
import com.huyiyu.auth.service.PolicyMatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PbacConfig {

  @Bean
  public UriPolicyAuthorizationManager uriPolicyAuthorizationManager(
      PolicyMatcher<HttpServletRequest, Long> uriPolicyMatcher) {
    return new UriPolicyAuthorizationManager(uriPolicyMatcher);
  }
}
