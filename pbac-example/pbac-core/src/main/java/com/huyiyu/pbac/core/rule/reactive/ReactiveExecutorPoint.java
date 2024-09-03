package com.huyiyu.pbac.core.rule.reactive;

import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.domain.PbacContext;
import reactor.core.publisher.Mono;

public interface ReactiveExecutorPoint<P> {

  Mono<PbacContext> getPolicyRuleParam(P pattern, PbacUser user);

}
