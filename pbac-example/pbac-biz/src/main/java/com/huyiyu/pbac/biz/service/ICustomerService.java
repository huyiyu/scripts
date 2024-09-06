package com.huyiyu.pbac.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.biz.entity.Customer;
import java.util.List;

/**
 * <p>
 * 客户表 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
public interface ICustomerService extends IService<Customer> {

  List<String> identityListByAccountId(Long accountId);

  boolean isCustomer(Long accountId);
}
