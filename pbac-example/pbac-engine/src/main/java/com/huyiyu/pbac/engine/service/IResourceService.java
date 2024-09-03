package com.huyiyu.pbac.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.engine.entity.Resource;

/**
 * <p>
 * 请求客户端资源 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
public interface IResourceService extends IService<Resource> {

  PbacRuleResult getRuleResultByPattern(String pattern);
}
