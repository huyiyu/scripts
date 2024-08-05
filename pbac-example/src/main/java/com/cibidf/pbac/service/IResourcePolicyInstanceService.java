package com.cibidf.pbac.service;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cibidf.pbac.entity.ResourcePolicyInstance;
import java.util.List;

/**
 * <p>
 * 规则资源关联表 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
public interface IResourcePolicyInstanceService extends IService<ResourcePolicyInstance> {

  List<Pair<Long, String>> listPairByResourceId(Long resId);
}
