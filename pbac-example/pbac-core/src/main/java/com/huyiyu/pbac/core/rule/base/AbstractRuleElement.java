package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacPolicyRule;
import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.enums.ConditionType;
import com.huyiyu.pbac.core.utils.JsonUtil;
import java.lang.reflect.ParameterizedType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractRuleElement<C> implements BeanNameAware {

  @Override
  public final void setBeanName(String name) {
    this.beanName = beanName;
  }

  private String beanName;

  private final Class<C> paramType;


  private static final Object NONE_PARAM = new Object();

  protected AbstractRuleElement() {
    ParameterizedType genericSuperclass = (ParameterizedType) this.getClass()
        .getGenericSuperclass();
    paramType = (Class<C>) genericSuperclass.getActualTypeArguments()[0];
  }

  public abstract boolean decide(PbacContext ruleContext, C configuration);


  public void next(PbacContext ruleContext, PbacPolicyRule policyRuleParam,
      SimpleRuleChain ruleChain) {
    boolean noneParam = paramType.isAssignableFrom(Object.class);
    C configuration = StringUtils.isNoneBlank() ? (C) NONE_PARAM
        : JsonUtil.json2Object(policyRuleParam.getValue(), paramType);
    boolean decide = decide(ruleContext, configuration);
    if ((!decide && policyRuleParam.getConditionType() == ConditionType.AND)
        || (decide && policyRuleParam.getConditionType() == ConditionType.OR)) {
      ruleChain.end(decide);
    } else {
      ruleChain.next(ruleContext);
    }
  }

  public final String getBeanName() {
    return beanName;
  }

  public final boolean checkParam(String paramString) {
    try {
      C c = JsonUtil.json2Object(paramString, paramType);
      return true;
    } catch (Exception e) {
      return false;
    }
  }


}
