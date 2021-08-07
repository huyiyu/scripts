package com.huyiyu.pbac.engine.service;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.engine.entity.PolicyInstance;
import java.util.List;

/**
 * <p>
 * 策略表,规定了configuration的内容 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
public interface IPolicyInstanceService extends IService<PolicyInstance> {

  List<Pair<Long, String>> listPolicyIdByIds(List<Long> list);
}
