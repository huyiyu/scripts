package com.huyiyu.pbac.core.domain;

import java.util.List;
import lombok.Data;

@Data
public class PbacRuleResult {

  private Long resourceId;
  private Long policyId;
  private String resourceName;
  private List<PbacPolicyRule> policyRuleParams;

}
