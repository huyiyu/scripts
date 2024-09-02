package com.huyiyu.pbac.gateway.pbac;

import com.huyiyu.pbac.core.domain.Resource;
import com.huyiyu.pbac.core.domain.PolicyRuleParam;
import java.util.List;
import lombok.Data;

@Data
public class RuleResult {

  private Long resourceId;
  private Long policyId;
  private String resourceName;
  private String policyName;
  private List<PolicyRuleParam> policyRuleParams;

}
