package com.cib.gpt.manager.http.annotation;

import com.cib.gpt.manager.http.ExchangeRegistrar;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ExchangeRegistrar.class)
public @interface EnableHttpExchange {

  String proxyBeanName();

  String[] basePackages();

}
