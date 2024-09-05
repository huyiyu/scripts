package com.huyiyu.pbac.core.domain;

import com.huyiyu.pbac.core.enums.ConditionType;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ToString
public class PbacRuleResult implements Serializable {
  private Long policyId;
  private Long resourceId;
  private List<PbacPolicyRule> policyRuleParams;

  @Data
  public static class PbacPolicyRule implements Serializable {

    private String handlerName;
    private String script;
    private String value;
    private ConditionType conditionType;
  }
}
