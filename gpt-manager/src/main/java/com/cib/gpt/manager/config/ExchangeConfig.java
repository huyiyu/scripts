package com.cib.gpt.manager.config;

import com.cib.gpt.manager.http.annotation.EnableHttpExchange;
import com.cib.gpt.manager.http.utils.ProxyFactoryUtil;
import com.cib.gpt.manager.property.AppProperties;
import com.cib.gpt.manager.property.AppProperties.Fastgpt;
import java.time.Duration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableHttpExchange(proxyBeanName = "fastgptExchangeProxyFactory", basePackages = "com.cib.gpt.manager.exchange.service")
public class ExchangeConfig {

  private static final String AUTHORIZATION_PREFIX = "Bearer ";

  @Bean
  public HttpServiceProxyFactory fastgptExchangeProxyFactory(AppProperties appProperties) {
    Fastgpt fastgpt = appProperties.getFastgpt();
    RestClient restClient = RestClient.builder()
        .requestFactory(ClientHttpRequestFactoryBuilder.httpComponents()
            .withCustomizer(customizer -> customizer.setConnectTimeout(Duration.ofSeconds(5)))
            .withCustomizer(customizer -> customizer.setReadTimeout(Duration.ofSeconds(5)))
            .build()
        ).baseUrl(fastgpt.getBaseurl())
        .defaultHeader(HttpHeaders.AUTHORIZATION, AUTHORIZATION_PREFIX + fastgpt.getApikey())
        .build();
    return ProxyFactoryUtil.factoryByRestClient(restClient);
  }

}
