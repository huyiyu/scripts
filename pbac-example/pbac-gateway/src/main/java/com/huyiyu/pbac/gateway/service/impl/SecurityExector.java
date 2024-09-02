package com.huyiyu.pbac.gateway.service.impl;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.rule.base.RuleChainFactory;
import com.huyiyu.pbac.gateway.pbac.URIReactiveExecutorPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SecurityExector implements ReactiveAuthorizationManager<AuthorizationContext> {

  private final RuleChainFactory ruleChainFactory;
  private final URIReactiveExecutorPoint uriReactiveExecutorPoint;

  @Override
  public Mono<AuthorizationDecision> check(Mono<Authentication> authentication,
      AuthorizationContext authorizationContext) {
    ServerWebExchange exchange = authorizationContext.getExchange();
    return authentication.filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .cast(LoginUser.class)
        .flatMap(loginUser -> ruleChainFactory.decide(uriReactiveExecutorPoint, authorizationContext, loginUser))
        .map(AuthorizationDecision::new);
  }
}
