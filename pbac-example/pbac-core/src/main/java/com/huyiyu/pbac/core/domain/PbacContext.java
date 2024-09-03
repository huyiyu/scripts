package com.huyiyu.pbac.core.domain;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PbacContext {

  private Object pattern;
  private Long resourceId;
  private Long policyId;
  private String resourceName;
  private List<PbacPolicyRule> policyRuleParamList;
  private PbacUser loginUser;

}
