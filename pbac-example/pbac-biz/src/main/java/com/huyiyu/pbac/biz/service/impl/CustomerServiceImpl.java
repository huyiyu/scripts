package com.huyiyu.pbac.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.biz.entity.Customer;
import com.huyiyu.pbac.biz.mapper.CustomerMapper;
import com.huyiyu.pbac.biz.service.ICustomerService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-07-31
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements
    ICustomerService {

}
