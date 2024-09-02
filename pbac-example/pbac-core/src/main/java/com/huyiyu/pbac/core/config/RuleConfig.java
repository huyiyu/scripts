package com.huyiyu.pbac.core.config;

import com.huyiyu.pbac.core.rule.base.AbstractRuleElement;
import com.huyiyu.pbac.core.rule.base.RuleChainFactory;
import com.huyiyu.pbac.core.rule.base.impl.GrooovyRule;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RuleConfig {
  
  @Bean
  public GrooovyRule groovyRule() {
    return new GrooovyRule();
  }

  @Bean
  public RuleChainFactory ruleChainFactory(Map<String, AbstractRuleElement<?>> ruleElementMap) {
    return new RuleChainFactory(ruleElementMap);
  }

}
