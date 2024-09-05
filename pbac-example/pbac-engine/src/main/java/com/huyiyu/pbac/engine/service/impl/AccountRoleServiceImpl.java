package com.huyiyu.pbac.engine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.engine.entity.AccountRole;
import com.huyiyu.pbac.engine.mapper.AccountRoleMapper;
import com.huyiyu.pbac.engine.service.IAccountRoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 成交单 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-07-31
 */
@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl extends ServiceImpl<AccountRoleMapper, AccountRole> implements IAccountRoleService {


  @Override
  public List<String> listRoleCodesByAccountId(long accountId){
    return lambdaQuery()
        .select(AccountRole::getRoleCode)
        .eq(AccountRole::getAccountId, accountId)
        .list()
        .stream()
        .map(AccountRole::getRoleCode)
        .toList();
  }
}
