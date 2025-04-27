package com.cib.gpt.manager.http;

import java.util.Optional;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Setter
public class ExchangeFactoryBean<T> implements FactoryBean<T> {

  private Class<T> exchangeClass;
  private Optional<HttpServiceProxyFactory> httpServiceProxyFactory;

  @Override
  public T getObject() throws Exception {
    Assert.isTrue(!httpServiceProxyFactory.isEmpty(), "httpServiceProxyFactory 未注册");

    return httpServiceProxyFactory.map(this::createClient).get();
  }

  private T createClient(HttpServiceProxyFactory httpServiceProxyFactory) {
    return httpServiceProxyFactory.createClient(exchangeClass);
  }

  @Override
  public Class<?> getObjectType() {
    return exchangeClass;
  }
}
