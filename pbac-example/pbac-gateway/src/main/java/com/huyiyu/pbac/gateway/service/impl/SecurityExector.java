package com.huyiyu.pbac.gateway.service.impl;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SecurityExector implements ReactiveAuthorizationManager<ServerWebExchange> {

  @Override
  public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, ServerWebExchange object) {




    return null;
  }
}
