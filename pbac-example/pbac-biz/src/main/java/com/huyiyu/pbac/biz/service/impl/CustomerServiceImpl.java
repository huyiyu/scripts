package com.huyiyu.pbac.biz.service.impl;

import com.alibaba.nacos.api.config.filter.IConfigFilter;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.biz.entity.Customer;
import com.huyiyu.pbac.biz.enums.IdentityType;
import com.huyiyu.pbac.biz.mapper.CustomerMapper;
import com.huyiyu.pbac.biz.service.ICustomerService;
import com.huyiyu.pbac.biz.service.IHouseManagementAdminService;
import com.huyiyu.pbac.biz.service.ISalesmanService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * <p>
 * 客户表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-07-31
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

  private final static String PBAC_IDENTITY_PREFIX = "PBAC::IDENTITY::";

  private final ISalesmanService salesmanService;
  private final IHouseManagementAdminService houseManagementAdminService;
  private final RedisTemplate redisTemplate;


  @Override
  public Collection<String> identityListByAccountId(Long accountId) {
    String key = PBAC_IDENTITY_PREFIX + accountId;
    if (!redisTemplate.hasKey(key)){
      synchronized (key){
        if (!redisTemplate.hasKey(key)){
          List<String> identityList = identityListByAccountIdFromDB(accountId);
          if (!CollectionUtils.isEmpty(identityList)){
            redisTemplate.opsForSet().add(key, identityList.toArray());
          }
        }
      }
    }
    return (Collection<String>) redisTemplate.opsForSet().members(key);
  }

  private List<String> identityListByAccountIdFromDB(Long accountId) {
    List<String> identityList = new ArrayList<>();
    if (isCustomer(accountId)) {
      identityList.add(IdentityType.CUSTOMER.name());
    }
    return identityList;
  }

  @Override
  public boolean isCustomer(Long accountId) {
    return lambdaQuery()
        .select(Customer::getId)
        .eq(Customer::getAccountId, accountId)
        .exists();
  }
}
