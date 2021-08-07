package com.huyiyu.pbac.engine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.engine.entity.PolicyDefine;
import com.huyiyu.pbac.engine.mapper.PolicyDefineMapper;
import com.huyiyu.pbac.engine.service.IPolicyDefineService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 鉴权规则表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
@Service
public class PolicyDefineServiceImpl extends ServiceImpl<PolicyDefineMapper, PolicyDefine> implements IPolicyDefineService {

}
