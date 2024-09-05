package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.utils.JsonUtil;
import java.lang.reflect.ParameterizedType;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractGenericRule<C> implements IPbacRule {

  private final Class<C> paramType;

  protected AbstractGenericRule() {
    ParameterizedType genericSuperclass = (ParameterizedType) this.getClass()
        .getGenericSuperclass();
    paramType = (Class<C>) genericSuperclass.getActualTypeArguments()[0];
  }

  @Override
  public boolean decide(PbacContext ruleContext, String configuration) {
    C c = StringUtils.isNotBlank(configuration) ? JsonUtil.json2Object(configuration, paramType) : null;
    return decideWithType(ruleContext, c);
  }

  public abstract boolean decideWithType(PbacContext ruleContext, C configuration);

}
