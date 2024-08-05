package com.cibidf.pbac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cibidf.pbac.auth.domain.LoginUser;
import com.cibidf.pbac.entity.Account;
import com.cibidf.pbac.mapper.AccountMapper;
import com.cibidf.pbac.service.IAccountRoleService;
import com.cibidf.pbac.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements
    IAccountService, UserDetailsService {

  private final IAccountRoleService accountRoleService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return lambdaQuery()
        .select(Account::getId, Account::getUsername, Account::getPassword)
        .eq(Account::getUsername, username)
        .oneOpt()
        .map(account -> {
          LoginUser loginUser = new LoginUser();
          loginUser.putttribute(LoginUser.USERNAME_KEY, account.getUsername());
          loginUser.putttribute(LoginUser.PASSWORD_KEY, account.getPassword());
          loginUser.putttribute(LoginUser.ACCOUNT_ID_KEY, account.getId());
          loginUser.putttribute(LoginUser.ROLE_NAMES_KEY, accountRoleService.listNamesByAccountId(account.getId()));
          return loginUser;
        }).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
  }
}
