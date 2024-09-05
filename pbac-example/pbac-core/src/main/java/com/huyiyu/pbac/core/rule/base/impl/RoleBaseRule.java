package com.huyiyu.pbac.core.rule.base.impl;

import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.rule.base.IPbacRule;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

public abstract class RoleBaseRule implements IPbacRule {

  public abstract List<String> listRoleCodesByResourceId(Long resourceId);

  @Override
  public boolean decide(PbacContext pbacContext, String configuration) {
    List<String> roleCodes = listRoleCodesByResourceId(pbacContext.getResourceId());
    return pbacContext.getPbacUser().getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(roleCodes::contains);
  }

}
