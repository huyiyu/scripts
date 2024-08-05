package com.huyiyu.auth.service;

import com.huyiyu.auth.domain.PolicyUser;
import com.huyiyu.auth.domain.impl.AttributeUser;

public interface PolicyHandler {

  boolean decide(PolicyUser user, String policyParam);

}
