package com.huyiyu.pbac.engine.convert;

import com.huyiyu.pbac.core.domain.PbacResource;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.core.domain.PbacRuleResult.PbacPolicyRule;
import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.engine.dto.RuleNameScriptDTO;
import com.huyiyu.pbac.engine.entity.Account;
import com.huyiyu.pbac.engine.entity.PolicyRule;
import com.huyiyu.pbac.engine.entity.Resource;
import com.huyiyu.pbac.engine.entity.Rule;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PbacConvertor {

  PbacConvertor INSTANCE = Mappers.getMapper(PbacConvertor.class);

  PbacResource resource2PbacResource(Resource resource);

  @Mapping(target = "handlerName", source = "ruleNameScriptDTO.handlerName")
  @Mapping(target = "scripts", source = "ruleNameScriptDTO.scripts")
  @Mapping(target = "value", source = "policyRule.paramValue")
  @Mapping(target = "conditionType", expression = "java(com.huyiyu.pbac.core.enums.ConditionType.of(policyRule.getConditionType()))")
  PbacPolicyRule policyRuleAndRuleNameScriptDTO2PbacPolicyRule(PolicyRule policyRule,
      RuleNameScriptDTO ruleNameScriptDTO);

  @Mapping(target = "username",source = "account.username")
  @Mapping(target = "password",source = "account.password")
  @Mapping(target = "authorities",ignore = true)
  @Mapping(target = "accountId",source = "account.id")
  @Mapping(target = "roleCodes",source = "roleCodes")
  PbacUser account2LoginUser(Account account, List<String> roleCodes);

  RuleNameScriptDTO rule2RuleNameScriptDTO(Rule rule);
}
