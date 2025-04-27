package com.cib.gpt.manager.http;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;


public class ExchangeScanner extends ClassPathBeanDefinitionScanner {

  private String proxyBeanName;

  public ExchangeScanner(BeanDefinitionRegistry registry, String proxyBeanName) {
    super(registry, false);
    if (registry instanceof DefaultListableBeanFactory defaultListableBeanFactory) {
      Environment environment = defaultListableBeanFactory.getBean(Environment.class);
      super.setEnvironment(environment);
    }
    this.addIncludeFilter(new AnnotationTypeFilter(HttpExchange.class));
    this.proxyBeanName = proxyBeanName;
  }

  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return true;
  }


  @Override
  protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
    String beanClassName = beanDefinition.getBeanClassName();
    beanDefinition.setBeanClass(ExchangeFactoryBean.class);
    MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
    try {
      propertyValues.addPropertyValue("exchangeClass", Class.forName(beanClassName));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    propertyValues.addPropertyValue("httpServiceProxyFactory", new RuntimeBeanReference(proxyBeanName));
    propertyValues.addPropertyValue("proxyBeanName", proxyBeanName);
    super.postProcessBeanDefinition(beanDefinition, beanName);


  }
}
