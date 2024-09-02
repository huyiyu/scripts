package com.huyiyu.pbac.core.rule.base;

import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.domain.PolicyRuleParam;
import com.huyiyu.pbac.core.domain.Resource;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RuleContext {

  private Object pattern;
  private Long resourceId;
  private Long policyId;
  private String resourceName;
  private String policyName;
  private List<PolicyRuleParam> policyRuleParamList;
  private LoginUser loginUser;

}
