package com.huyiyu.pbac.gateway.pbac;


import static com.huyiyu.pbac.core.constant.PbacConstant.PBAC_PATH_PREFIX;
import static com.huyiyu.pbac.core.constant.PbacConstant.PBAC_POLICY_ID_PREFIX;

import com.huyiyu.pbac.core.domain.PbacResource;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.exception.BusiPbacException;
import com.huyiyu.pbac.core.rule.reactive.ReactiveExecutorPoint;
import com.huyiyu.pbac.gateway.domain.R;
import java.text.MessageFormat;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class URIReactiveExecutorPoint implements ReactiveExecutorPoint<AuthorizationContext> {

  private final WebClient webClient;
  private final ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate;

  private final String SERVICE_NAME_REGEX = "/pbac-\\w+(/.+)";

  @Override
  public Mono<PbacRuleResult> getPolicyRuleParam(AuthorizationContext pattern,
      PbacUser user) {
    ServerHttpRequest request = pattern.getExchange().getRequest();
    String path = getRootPattern(request);
    return this.getFromCache(path)
        .switchIfEmpty(httpForRuleResult(path));

  }

  /**
   * 去掉首层服务名
   *
   * @param request
   * @return
   */
  private String getRootPattern(ServerHttpRequest request) {
    String value = request.getPath().value();
    return value.replaceAll(SERVICE_NAME_REGEX, "$1");
  }


  private Mono<PbacRuleResult> httpForRuleResult(String path) {
    return webClient.get()
        .uri("http://pbac-engine/resource/getRuleResultByPattern?pattern={path}", Map.of("path",path))
        .exchangeToMono(clientResponse -> clientResponse
                .bodyToMono(new ParameterizedTypeReference<R<PbacRuleResult>>() {
            }))
        .map(R::orElseThrowException);
  }

  public Mono<PbacRuleResult> getFromCache(String path) {
    return reactiveRedisTemplate
        .opsForValue()
        .get(PBAC_PATH_PREFIX + path)
        .cast(PbacResource.class)
        .flatMap(pbacResource -> reactiveRedisTemplate.opsForValue()
            .get(PBAC_POLICY_ID_PREFIX + pbacResource.getPolicyId())
            .cast(PbacRuleResult.class)
        );
  }


}
