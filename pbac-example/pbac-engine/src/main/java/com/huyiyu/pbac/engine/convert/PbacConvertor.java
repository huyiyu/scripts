package com.huyiyu.pbac.engine.convert;

import com.huyiyu.pbac.core.domain.PbacPolicyRule;
import com.huyiyu.pbac.core.domain.PbacResource;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.engine.dto.RuleNameScriptDTO;
import com.huyiyu.pbac.engine.entity.Account;
import com.huyiyu.pbac.engine.entity.PolicyRule;
import com.huyiyu.pbac.engine.entity.Resource;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PbacConvertor {

  PbacConvertor INSTANCE = Mappers.getMapper(PbacConvertor.class);


  PbacResource resource2PbacResource(Resource resource);

  @Mapping(target = "resourceId", source = "pbacResource.id")
  @Mapping(target = "policyId", source = "pbacResource.policyId")
  @Mapping(target = "resourceName", source = "pbacResource.name")
  @Mapping(target = "policyRuleParams", source = "pbacPolicyRules")
  PbacRuleResult pbacResourceAndList2PbacRuleResult(List<PbacPolicyRule> pbacPolicyRules,
      PbacResource pbacResource);

  @Mapping(target = "handlerName", source = "ruleNameScriptDTO.handlerName")
  @Mapping(target = "script", source = "ruleNameScriptDTO.script")
  @Mapping(target = "value", source = "policyRule.paramValue")
  @Mapping(target = "conditionType", expression = "java(com.huyiyu.pbac.core.enums.ConditionType.of(policyRule.getConditionType()))")
  PbacPolicyRule policyRuleAndRuleNameScriptDTO2PbacPolicyRule(PolicyRule policyRule,
      RuleNameScriptDTO ruleNameScriptDTO);

  PbacUser account2LoginUser(Account account);
}
