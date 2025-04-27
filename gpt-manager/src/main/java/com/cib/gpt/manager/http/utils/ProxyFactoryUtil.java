package com.cib.gpt.manager.http.utils;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class ProxyFactoryUtil {

  public static HttpServiceProxyFactory factoryByRestClient(RestClient restClient) {
    return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
  }

  public static HttpServiceProxyFactory factoryByWebClient(WebClient webClient) {
    return HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
  }

}
