package com.huyiyu.pbac.actuator.config;

import com.huyiyu.pbac.actuator.filter.HttpBasicFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<HttpBasicFilter> http11Filter() {
        HttpBasicFilter httpBasicFilter = new HttpBasicFilter();
        FilterRegistrationBean<HttpBasicFilter> httpBasicFilterFilterRegistrationBean = new FilterRegistrationBean<>(httpBasicFilter);
        httpBasicFilterFilterRegistrationBean.setOrder(1);
        httpBasicFilterFilterRegistrationBean.addUrlPatterns("/*");
        return httpBasicFilterFilterRegistrationBean;
    }
}
