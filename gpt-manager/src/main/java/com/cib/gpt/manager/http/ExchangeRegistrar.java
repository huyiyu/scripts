package com.cib.gpt.manager.http;

import com.cib.gpt.manager.http.annotation.EnableHttpExchange;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;


public class ExchangeRegistrar implements ImportBeanDefinitionRegistrar {


  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
    AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
        importingClassMetadata.getAnnotationAttributes(EnableHttpExchange.class.getName()));
    String[] basePackages = annotationAttributes.getStringArray("basePackages");
    String proxyBeanName = annotationAttributes.getString("proxyBeanName");
    AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(
            ExchangeScannerConfigurer.class)
        .addPropertyValue("basePackages", basePackages)
        .addPropertyValue("proxyBeanName", proxyBeanName)
        .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
        .getBeanDefinition();
    String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
    registry.registerBeanDefinition(beanName, beanDefinition);
  }
}
