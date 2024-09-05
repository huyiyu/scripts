package com.huyiyu.pbac.core.rule.reactive;

import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.domain.PbacContext;
import reactor.core.publisher.Mono;

public interface ReactiveExecutorPoint<P> {

  Mono<PbacRuleResult> getPolicyRuleParam(P pattern, PbacUser user);

}
