package com.cibidf.pbac.service.impl;

import com.cibidf.pbac.entity.Customer;
import com.cibidf.pbac.mapper.CustomerMapper;
import com.cibidf.pbac.service.ICustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

}
