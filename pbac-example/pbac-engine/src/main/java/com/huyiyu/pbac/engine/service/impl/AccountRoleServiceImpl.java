package com.huyiyu.pbac.engine.service.impl;

import com.huyiyu.pbac.engine.entity.AccountRole;
import com.huyiyu.pbac.engine.mapper.AccountRoleMapper;
import com.huyiyu.pbac.engine.service.IAccountRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房管局审核员 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-01
 */
@Service
public class AccountRoleServiceImpl extends ServiceImpl<AccountRoleMapper, AccountRole> implements
    IAccountRoleService {



  @Override
  public List<String> listNamesByAccountId(Long id) {
    List<AccountRole> list = lambdaQuery().select(AccountRole::getRoleCode)
        .eq(AccountRole::getAccountId, id)
        .list();
    return list.stream().map(AccountRole::getRoleCode).toList();
  }
}
