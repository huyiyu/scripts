package com.cibidf.pbac.config;

import com.cibidf.pbac.auth.domain.LoginUser;
import com.cibidf.pbac.auth.jwt.JwtService;
import com.cibidf.pbac.auth.manage.uri.UriPolicyAuthorizationManager;
import com.cibidf.pbac.auth.trace.TraceIdHeaderWriter;
import com.cibidf.pbac.result.R;
import com.cibidf.pbac.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue;


@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

  private static final String JWT_HEADER = "JWT";

  private final JwtService jwtService;

  private final UserDetailsService userDetailsService;


  @Bean
  public SecurityFilterChain springFilter(HttpSecurity http,
      UriPolicyAuthorizationManager uriPolicyAuthorizationManager)
      throws Exception {
    return http
        .oauth2ResourceServer(oauthServer -> oauthServer
            .bearerTokenResolver(new HeaderBearerTokenResolver(JWT_HEADER))
            .authenticationEntryPoint(this::loginFailure)
            .jwt(jwtConfigurer -> jwtConfigurer
                .decoder(jwtService)
                .jwtAuthenticationConverter(jwtService::convertJwt)
            ))
        .formLogin(formlogin -> formlogin
            .loginProcessingUrl("/auth/login")
            .successHandler(this::onLoginSuccess)
            .failureHandler(this::loginFailure)
        )
        .csrf(csrf -> csrf.disable())
        .headers(headersConfigurer -> headersConfigurer.xssProtection(
                xssConfig -> xssConfig.headerValue(HeaderValue.ENABLED_MODE_BLOCK)
            ).addHeaderWriter(new TraceIdHeaderWriter())
        )
        .logout(logout -> logout.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest()
            .access(uriPolicyAuthorizationManager)
        )
        .cors(cors -> cors.disable())
        .userDetailsService(userDetailsService)
        .sessionManagement(session -> session.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .accessDeniedHandler(this::accessDenied)
            .authenticationEntryPoint(this::loginFailure))
        .build();
  }


  private void onLoginSuccess(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
    LoginUser principal = (LoginUser) authentication.getPrincipal();
    String data = JsonUtil.object2Json(R.ok(jwtService.encode(principal)));
    IOUtils.write(data, httpServletResponse.getWriter());


  }

  private void loginFailure(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
    String result = JsonUtil.object2Json(R.fail(e.getMessage()));
    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    IOUtils.write(result, httpServletResponse.getWriter());
  }

  private void accessDenied(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, AccessDeniedException e)
      throws IOException {
    String result = JsonUtil.object2Json(R.fail("无权限访问"));
    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    IOUtils.write(result, httpServletResponse.getWriter());
  }


}
