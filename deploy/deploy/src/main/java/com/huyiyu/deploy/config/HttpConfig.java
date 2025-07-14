package com.huyiyu.deploy.config;

import com.huyiyu.deploy.http.ArtifactoryExchange;
import com.huyiyu.deploy.property.DeployProperties;
import com.huyiyu.deploy.property.DeployProperties.Repo;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DeployProperties.class)
public class HttpConfig {


  @Bean
  public RestClient.Builder restClientBuilder() {
    CloseableHttpClient build = HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.DEFAULT)
        .build();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
        build);
    requestFactory.setReadTimeout(Duration.ofSeconds(3));
    requestFactory.setConnectionRequestTimeout(Duration.ofSeconds(5));
    requestFactory.setConnectTimeout(Duration.ofSeconds(10));
    return RestClient.builder().requestFactory(requestFactory);
  }

  @Bean
  public ArtifactoryExchange artifactoryExchange(DeployProperties deployProperties,
      RestClient.Builder builder) {
    Repo repo = deployProperties.getRepo();
    String s = repo.getUsername() + ":" + repo.getPassword();
    String cert = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    builder
        .baseUrl(repo.getUrl())
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + cert);
    return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build()))
        .build().createClient(ArtifactoryExchange.class);
  }


}
