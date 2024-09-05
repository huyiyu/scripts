package com.huyiyu.pbac.engine.service.impl;

import com.huyiyu.pbac.engine.entity.Resource;
import com.huyiyu.pbac.engine.mapper.ResourceMapper;
import com.huyiyu.pbac.engine.service.IResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 请求客户端资源 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-05
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {

}
