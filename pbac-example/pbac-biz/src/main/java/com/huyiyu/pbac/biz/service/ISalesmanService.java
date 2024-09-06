package com.huyiyu.pbac.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.biz.entity.Salesman;

/**
 * <p>
 * 销售员表 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
public interface ISalesmanService extends IService<Salesman> {

  boolean isSalesman(Long accountId);
}
