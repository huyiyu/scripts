package com.huyiyu.pbac.core.rule;

import com.huyiyu.pbac.core.enums.ConditionType;
import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.utils.JsonUtil;
import java.lang.reflect.ParameterizedType;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractRuleElement<C> {



  protected Class<C> paramType;

  private static final Object NONE_PARAM = new Object();

  protected AbstractRuleElement() {
    ParameterizedType genericSuperclass = (ParameterizedType) this.getClass()
        .getGenericSuperclass();
    paramType = (Class<C>) genericSuperclass.getActualTypeArguments()[0];
  }

  public abstract boolean decide(LoginUser loginUser, C configuration);


  public void next(LoginUser loginUser, PolicyRuleParam policyRuleParam, RuleChain ruleChain) {
    boolean noneParam = paramType.isAssignableFrom(Object.class);
    C configuration = StringUtils.isNoneBlank() ? (C) NONE_PARAM : JsonUtil.json2Object(policyRuleParam.getValue(), paramType);
    boolean decide = decide(loginUser, configuration);
    if ((!decide && policyRuleParam.getConditionType() == ConditionType.AND)
        || (decide && policyRuleParam.getConditionType() == ConditionType.OR)) {
      ruleChain.end(decide);
    } else {
      ruleChain.next();
    }
  }



}
