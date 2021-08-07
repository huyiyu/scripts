package com.huyiyu.pbac.engine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.engine.entity.Role;
import com.huyiyu.pbac.engine.mapper.RoleMapper;
import com.huyiyu.pbac.engine.service.IRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房管局审核员 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
