package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.enums.ConditionType;

public interface IPbacRule {

  boolean decide(PbacContext pbacContext, String configuration);

  default void executeRule(PbacContext pbacContext, SimpleRuleChain ruleChain, String configuration) {
    boolean decide = decide(pbacContext,configuration);
    // 结果需要与上一结果与操作
    pbacContext.setResult(pbacContext.getResult() && decide);
    // 如果规则满足且连接是AND说明要接着判断(必要条件为true)
    // 同理规则不满足是OR,则说明要接着判断(充分条件为false)
    boolean needNext =
        (decide && pbacContext.getCurrentRuleConditionType().equals(ConditionType.AND))
            || (!decide && pbacContext.getCurrentRuleConditionType().equals(ConditionType.OR));
    if (needNext) {
      ruleChain.executeRule(pbacContext);
    }
  }

}
