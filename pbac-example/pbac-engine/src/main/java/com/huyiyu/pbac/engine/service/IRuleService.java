package com.huyiyu.pbac.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.engine.dto.RuleNameScriptDTO;
import com.huyiyu.pbac.engine.entity.Rule;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 鉴权规则表 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
public interface IRuleService extends IService<Rule> {

  Map<Long, RuleNameScriptDTO> getHandlerNameAndScriptMapByRuleIds(Set<Long> ruleList);
}
