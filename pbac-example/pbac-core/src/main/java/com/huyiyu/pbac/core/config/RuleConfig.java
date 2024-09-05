package com.huyiyu.pbac.core.config;

import com.huyiyu.pbac.core.rule.base.IPbacRule;
import com.huyiyu.pbac.core.rule.base.RuleChainFactory;
import com.huyiyu.pbac.core.rule.base.impl.DailyStartEndRule;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RuleConfig {

  @Bean
  public DailyStartEndRule dailyStartEndRule(){
    return new DailyStartEndRule();
  }

  @Bean
  public RuleChainFactory ruleChainFactory(Map<String, IPbacRule> ruleElementMap) {
    return new RuleChainFactory(ruleElementMap);
  }

}
