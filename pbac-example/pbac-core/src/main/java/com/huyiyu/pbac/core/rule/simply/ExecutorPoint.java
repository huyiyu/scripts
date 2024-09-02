package com.huyiyu.pbac.core.rule.simply;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.domain.PolicyRuleParam;
import com.huyiyu.pbac.core.rule.base.RuleContext;
import java.util.List;
import reactor.core.publisher.Mono;

public interface ExecutorPoint<P>{

  RuleContext getPolicyRuleParam(P pattern, LoginUser user);
}
