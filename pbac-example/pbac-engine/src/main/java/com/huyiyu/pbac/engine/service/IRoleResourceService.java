package com.huyiyu.pbac.engine.service;

import com.huyiyu.pbac.engine.entity.RoleResource;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 角色资源表 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-06
 */
public interface IRoleResourceService extends IService<RoleResource> {

  List<String> roleCodesByResourceId(Long id);
}
