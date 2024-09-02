package com.huyiyu.pbac.core.rule.reactive;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.rule.base.RuleContext;
import reactor.core.publisher.Mono;

public interface ReactiveExecutorPoint<P> {

  Mono<RuleContext> getPolicyRuleParam(P pattern, LoginUser user);

}
