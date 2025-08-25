package com.huyiyu.pbac.gateway.config;

import com.huyiyu.pbac.core.jwt.JwtService;
import com.huyiyu.pbac.core.property.PbacProperties;
import com.huyiyu.pbac.core.utils.JsonUtil;
import com.huyiyu.pbac.gateway.domain.R;
import com.huyiyu.pbac.gateway.service.impl.SecurityExector;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

  private final static String JWT = "JWT";


  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
      PbacProperties pbacProperties, JwtService jwtService, SecurityExector securityExector) {

    return http.oauth2ResourceServer(oAuth2ResourceServerSpec ->
            oAuth2ResourceServerSpec
                .bearerTokenConverter(serverWebExchange->bareTokenConvert(serverWebExchange,pbacProperties))
                .jwt(jwtSpec -> jwtSpec
                        .authenticationManager()
                        .jwtDecoder(token -> Mono.just(jwtService.decode(token))))
        ).csrf(csrf -> csrf.disable())
        .logout(logout -> logout.disable())
        .cors(cors -> cors.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .authorizeExchange(authorizeExchangeSpec ->
            authorizeExchangeSpec
                .pathMatchers(pbacProperties.getPermitAllPattern()).permitAll()
                .anyExchange()
                .access(securityExector)
        ).exceptionHandling(exceptionHandlingSpec ->
            exceptionHandlingSpec.accessDeniedHandler(this::accessDenied)
                .authenticationEntryPoint(this::onLoginFailure)
        )
        .build();
  }


  private Mono<Authentication> bareTokenConvert(ServerWebExchange serverWebExchange,
      PbacProperties pbacProperties) {
    String value = serverWebExchange.getRequest().getPath().value();
    if (Arrays.stream(pbacProperties.getPermitAllPattern())
            .anyMatch(str -> str.equals(value))) {
      return Mono.empty();
    }
    String first = serverWebExchange
        .getRequest()
        .getHeaders()
        .getFirst(JWT);
    return Mono.justOrEmpty(Optional.ofNullable(first).map(BearerTokenAuthenticationToken::new));
  }

  private Mono<Void> onLoginFailure(ServerWebExchange serverWebExchange,
      AuthenticationException e) {
    ServerHttpResponse response = serverWebExchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return writeResponse(R.fail(e.getMessage()), response);
  }

  private Mono<Void> accessDenied(ServerWebExchange serverWebExchange, AccessDeniedException e) {
    ServerHttpResponse response = serverWebExchange.getResponse();
    response.setStatusCode(HttpStatus.FORBIDDEN);
    return writeResponse(R.fail(e.getMessage()), response);
  }

  private Mono<Void> writeResponse(R r, ServerHttpResponse response) {
    return response
        .writeWith(Mono.just(response.bufferFactory().wrap(JsonUtil.object2Byte(r))));
  }

}
