package com.huyiyu.pbac.engine.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.engine.convert.PbacConvertor;
import com.huyiyu.pbac.engine.dto.RuleNameScriptDTO;
import com.huyiyu.pbac.engine.entity.Rule;
import com.huyiyu.pbac.engine.mapper.RuleMapper;
import com.huyiyu.pbac.engine.service.IRuleService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 鉴权规则表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
@Service
@RequiredArgsConstructor
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements IRuleService{

  @Override
  public Map<Long, RuleNameScriptDTO> getHandlerNameAndScriptMapByRuleIds(Set<Long> ruleList) {
    return lambdaQuery()
        .select(Rule::getId, Rule::getScripts, Rule::getHandlerName)
        .in(Rule::getId, ruleList)
        .list()
        .stream()
        .collect(Collectors.toMap(Rule::getId, PbacConvertor.INSTANCE::rule2RuleNameScriptDTO));
  }
}
