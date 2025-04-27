package com.cib.gpt.manager.http;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

@Slf4j
@Setter
public class ExchangeScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

  private String proxyBeanName;
  private String[] basePackages;


  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    ExchangeScanner exchangeScanner = new ExchangeScanner(registry, proxyBeanName);
    int scan = exchangeScanner.scan(basePackages);
    if (log.isDebugEnabled()) {
      log.debug("扫描获得了{}个Exchange", scan);
    }
  }
}
