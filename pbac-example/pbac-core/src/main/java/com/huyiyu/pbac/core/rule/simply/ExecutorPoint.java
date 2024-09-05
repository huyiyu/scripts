package com.huyiyu.pbac.core.rule.simply;

import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.domain.PbacContext;

public interface ExecutorPoint<P>{

  PbacRuleResult getPolicyRuleParam(P pattern, PbacUser user);
}
