package com.cibidf.pbac.auth.policy;

import com.huyiyu.auth.domain.PolicyUser;
import com.huyiyu.auth.service.PolicyHandler;

public class GroovyPolicyHandler implements PolicyHandler {

  public GroovyPolicyHandler(String scripts) {

  }

  @Override
  public boolean decide(PolicyUser user, String policyParam) {
    return false;
  }
}
