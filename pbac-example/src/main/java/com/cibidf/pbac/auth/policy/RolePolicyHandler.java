package com.cibidf.pbac.auth.policy;

import com.cibidf.pbac.auth.domain.LoginUser;
import com.cibidf.pbac.utils.JsonUtil;
import com.huyiyu.auth.domain.PolicyUser;
import com.huyiyu.auth.service.PolicyHandler;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class RolePolicyHandler implements PolicyHandler {

  @Data
  public static class RoleParam{
    private String roleCode;
  }


  @Override
  public boolean decide(PolicyUser user, String policyParam) {
    if (StringUtils.hasText(policyParam)) {
      RoleParam roleParam = JsonUtil.json2Object(policyParam, RoleParam.class);
      List<String> roles = (List<String>) user.attribute(LoginUser.ROLE_NAMES_KEY);
      return !CollectionUtils.isEmpty(roles) && roles.contains(roleParam.getRoleCode());
    }
    return false;
  }
}
