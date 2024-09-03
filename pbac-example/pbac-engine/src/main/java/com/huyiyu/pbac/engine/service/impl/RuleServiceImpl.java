package com.huyiyu.pbac.engine.service.impl;

import static com.huyiyu.pbac.engine.constant.PbacCacheConstant.PBAC_RULE;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.engine.dto.RuleNameScriptDTO;
import com.huyiyu.pbac.engine.entity.Rule;
import com.huyiyu.pbac.engine.mapper.RuleMapper;
import com.huyiyu.pbac.engine.service.IRuleService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements IRuleService {


  private final RedisTemplate redisTemplate;

  @Override
  public Map<Long, RuleNameScriptDTO> getHandlerNameAndScriptMapByRuleIds(List<Long> ruleList) {

    return Map.of();
  }
}
