package com.huyiyu.pbac.engine.service.impl;

import com.huyiyu.pbac.engine.entity.Policy;
import com.huyiyu.pbac.engine.mapper.PolicyMapper;
import com.huyiyu.pbac.engine.service.IPolicyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 策略表,规定了configuration的内容 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-05
 */
@Service
public class PolicyServiceImpl extends ServiceImpl<PolicyMapper, Policy> implements IPolicyService {

}
