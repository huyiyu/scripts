package com.huyiyu.pbac.gateway.service.impl;

import com.huyiyu.pbac.core.jwt.JwtService;
import com.huyiyu.pbac.core.rule.base.RuleChainFactory;
import com.huyiyu.pbac.gateway.pbac.URIReactiveExecutorPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SecurityExector implements ReactiveAuthorizationManager<AuthorizationContext> {

  private final URIReactiveExecutorPoint uriReactiveExecutorPoint;
  private final RuleChainFactory ruleChainFactory;
  private final JwtService jwtService;

  @Override
  public Mono<AuthorizationDecision> check(Mono<Authentication> authentication,
      AuthorizationContext authorizationContext) {
    ServerWebExchange exchange = authorizationContext.getExchange();
    return authentication.filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .cast(Jwt.class)
        .map(jwtService::jwt2PbacUser)
        .flatMap(pbacUser -> ruleChainFactory.decide(uriReactiveExecutorPoint, authorizationContext, pbacUser))
        .map(AuthorizationDecision::new);
  }
}
