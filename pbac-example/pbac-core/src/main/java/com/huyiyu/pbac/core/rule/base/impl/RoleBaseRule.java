package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.rule.base.IPbacRule;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

public abstract class RoleBaseRule implements IPbacRule {

  public abstract Collection<String> listRoleCodesByResourceId(Long resourceId);

  @Override
  public boolean decide(PbacContext pbacContext, String configuration) {
    Collection<String> roleCodes = listRoleCodesByResourceId(pbacContext.getResourceId());
    return pbacContext.getPbacUser().getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(roleCodes::contains);
  }

}
