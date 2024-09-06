package com.huyiyu.pbac.engine.service.impl;

import static com.huyiyu.pbac.core.constant.PbacConstant.PBAC_ROLE_CODES_PREFIX;

import com.huyiyu.pbac.core.constant.PbacConstant;
import com.huyiyu.pbac.engine.entity.RoleResource;
import com.huyiyu.pbac.engine.mapper.RoleResourceMapper;
import com.huyiyu.pbac.engine.service.IRoleResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色资源表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-06
 */
@Service
@RequiredArgsConstructor
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource> implements IRoleResourceService {

  private final RedisTemplate redisTemplate;

  @Override
  public List<String> roleCodesByResourceId(Long id) {
    String key = PBAC_ROLE_CODES_PREFIX + id;
    if (!redisTemplate.hasKey(key)){
      synchronized (key){
        if (!redisTemplate.hasKey(key)){
          List<String> roleCodes = roleCodesByResourceIdFromDB(id);
          redisTemplate.opsForSet().add(key,roleCodes.toArray());
        }
      }
    }
    return (List<String>) redisTemplate.opsForSet().members(key);
  }

  private List<String> roleCodesByResourceIdFromDB(Long id) {
    List<RoleResource> list = lambdaQuery()
        .select(RoleResource::getRoleCode)
        .eq(RoleResource::getResourceId, id)
        .list();
    return list.stream()
        .map(RoleResource::getRoleCode)
        .toList();
  }
}
