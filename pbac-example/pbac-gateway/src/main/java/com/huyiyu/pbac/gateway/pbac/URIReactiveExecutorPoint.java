package com.huyiyu.pbac.gateway.pbac;


import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.exception.BusiPbacException;
import com.huyiyu.pbac.core.rule.base.RuleContext;
import com.huyiyu.pbac.core.rule.reactive.ReactiveExecutorPoint;
import com.huyiyu.pbac.gateway.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class URIReactiveExecutorPoint implements ReactiveExecutorPoint<AuthorizationContext> {

  private final WebClient webClient;
  private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

  private Mono<RuleResult> httpForRuleResultAndPutCache(String path, String jwt) {
    return webClient.get()
        .header("JWT", jwt)
        .exchangeToMono(clientResponse -> clientResponse.bodyToMono(
                ResultUtil.resultType(RuleResult.class))
            .flatMap(result -> {
              if (result.getCode() == 0) {
                RuleResult data = result.getData();
                return reactiveRedisTemplate
                    .opsForValue()
                    .set(path, data)
                    .thenReturn(data);
              }
              throw new BusiPbacException("未发现权限");
            })
        );
  }

  @Override
  public Mono<RuleContext> getPolicyRuleParam(AuthorizationContext pattern,
      LoginUser user) {
    ServerHttpRequest request = pattern.getExchange().getRequest();
    String path = request.getPath().value();
    String jwt = request.getHeaders().getFirst("JWT");
    return reactiveRedisTemplate
        .opsForValue()
        .get(path)
        .cast(RuleResult.class)
        .or(this.httpForRuleResultAndPutCache(path, jwt))
        .map(ruleResult -> {
          RuleContext ruleContext = new RuleContext();
          ruleContext.setPolicyId(ruleResult.getPolicyId())
              .setPolicyName(ruleResult.getPolicyName())
              .setResourceId(ruleResult.getResourceId())
              .setResourceName(ruleResult.getResourceName())
              .setPolicyRuleParamList(ruleResult.getPolicyRuleParams())
              .setPattern(pattern)
              .setLoginUser(user);
          return ruleContext;
        });
  }
}
