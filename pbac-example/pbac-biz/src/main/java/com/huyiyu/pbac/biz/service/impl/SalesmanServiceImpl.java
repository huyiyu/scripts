package com.huyiyu.pbac.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.biz.entity.Salesman;
import com.huyiyu.pbac.biz.mapper.SalesmanMapper;
import com.huyiyu.pbac.biz.service.ISalesmanService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 销售员表 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-07-31
 */
@Service
public class SalesmanServiceImpl extends ServiceImpl<SalesmanMapper, Salesman> implements
    ISalesmanService {

  @Override
  public boolean isSalesman(Long accountId) {
    return lambdaQuery()
        .eq(Salesman::getAccountId, accountId)
        .exists();
  }
}
