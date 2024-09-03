package com.huyiyu.pbac.gateway.pbac;


import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.exception.BusiPbacException;
import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.rule.reactive.ReactiveExecutorPoint;
import com.huyiyu.pbac.gateway.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class URIReactiveExecutorPoint implements ReactiveExecutorPoint<AuthorizationContext> {

  private final WebClient webClient;
  private final ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate;

  @Override
  public Mono<PbacContext> getPolicyRuleParam(AuthorizationContext pattern,
      PbacUser user) {
    ServerHttpRequest request = pattern.getExchange().getRequest();
    String path = request.getPath().value();
    String jwt = request.getHeaders().getFirst("JWT");
    return this.getFromCache(path)
        .or(httpForRuleResult(path, jwt)
            .doOnNext(ruleResult -> putCache(path, ruleResult))
        )
        .map(ruleResult ->
            new PbacContext()
                .setPolicyId(ruleResult.getPolicyId())
                .setResourceId(ruleResult.getResourceId())
                .setResourceName(ruleResult.getResourceName())
                .setPolicyRuleParamList(ruleResult.getPolicyRuleParams())
                .setPattern(pattern)
                .setLoginUser(user)
        );
  }

  private Mono<Void> putCache(String path, PbacRuleResult ruleResult) {
    return reactiveRedisTemplate
        .opsForValue()
        .set(path, ruleResult.getResourceId())
        .and(reactiveRedisTemplate.opsForValue()
            .set(ruleResult.getResourceId(), ruleResult.getPolicyId()))
        .and(reactiveRedisTemplate.opsForValue()
            .set(ruleResult.getPolicyId(), ruleResult));
  }


  private Mono<PbacRuleResult> httpForRuleResult(String path, String jwt) {
    return webClient.get()
        .header("JWT", jwt)
        .exchangeToMono(clientResponse -> clientResponse.bodyToMono(
                ResultUtil.resultType(PbacRuleResult.class))
            .map(result -> {
              if (result.getCode() == 0) {
                return result.getData();
              }
              throw new BusiPbacException("未发现权限");
            })
        );
  }

  public Mono<PbacRuleResult> getFromCache(String path) {
    return reactiveRedisTemplate
        .opsForValue()
        .get(path)
        .cast(Long.class)
        .flatMap(resourceId -> reactiveRedisTemplate.opsForValue()
            .get(resourceId)
            .cast(Long.class)
            .flatMap(policyId -> reactiveRedisTemplate.opsForValue()
                .get(policyId)
                .cast(PbacRuleResult.class)
            )
        );
  }


}
