package com.huyiyu.pbac.core.rule.base;

import cn.hutool.core.lang.Assert;
import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.domain.PbacRuleResult.PbacPolicyRule;
import com.huyiyu.pbac.core.exception.BusiPbacException;
import com.huyiyu.pbac.core.exception.RuleNotFoundException;
import com.huyiyu.pbac.core.rule.base.impl.GrooovyRule;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SimpleRuleChain implements RuleChain {

  private int pos = 0;
  private List<PbacPolicyRule> policyRuleParams;
  private Map<String, IPbacRule> factory;

  public SimpleRuleChain(List<PbacPolicyRule> policyRuleParams, Map<String, IPbacRule> factory) {
    this.policyRuleParams = policyRuleParams;
    this.factory = factory;
  }


  private void internalExecuteRule(PbacContext ruleContext) {
    if (pos < policyRuleParams.size()) {
      PbacPolicyRule policyRuleParam = policyRuleParams.get(pos++);
      IPbacRule pbacRule = StringUtils.isNotBlank(policyRuleParam.getScripts()) ?
          new GrooovyRule(policyRuleParam.getScripts())
          : factory.get(policyRuleParam.getHandlerName());
      Assert.notNull(pbacRule, "规则名称不存在");
      ruleContext.setCurrentRuleConditionType(policyRuleParam.getConditionType());
      ruleContext.setCurrentRule(pbacRule);
      pbacRule.executeRule(ruleContext, this, policyRuleParam.getValue());
    }
  }

  @Override
  public void executeRule(PbacContext ruleContext) {
    internalExecuteRule(ruleContext);
  }
}
