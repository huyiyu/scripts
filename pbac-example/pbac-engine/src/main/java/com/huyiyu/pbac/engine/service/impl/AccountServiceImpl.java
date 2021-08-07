package com.huyiyu.pbac.engine.service.impl;

import cn.hutool.crypto.SmUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.engine.convert.AccountConvert;
import com.huyiyu.pbac.engine.entity.Account;
import com.huyiyu.pbac.engine.mapper.AccountMapper;
import com.huyiyu.pbac.engine.service.IAccountService;
import com.huyiyu.pbac.core.domain.LoginUser;
import com.huyiyu.pbac.core.exception.BusiPbacException;
import com.huyiyu.pbac.core.jwt.JwtService;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {

  private final JwtService jwtService;

  @Override
  public String login(String username, String password) {
    return findAccountByUsername(username)
        .filter(luser -> this.checkPassword(luser,password))
        .map(jwtService::encode)
        .orElseThrow(() -> new BusiPbacException("用户名或密码错误"));
  }

  private boolean checkPassword(LoginUser loginUser, String inputPassword) {
    String username = loginUser.getUsername();
    String digestedPassword = SmUtil.sm3WithSalt(username.getBytes(StandardCharsets.UTF_8))
        .digestHex(inputPassword.toString());
    return StringUtils.isNotBlank(loginUser.getPassword()) &&
        loginUser.getPassword().equals(digestedPassword);

  }

  private Optional<LoginUser> findAccountByUsername(String username) {
    return lambdaQuery()
        .select(Account::getId, Account::getUsername, Account::getPassword)
        .eq(Account::getUsername, username)
        .oneOpt()
        .map(AccountConvert.INSTANCE::account2LoginUser);


  }
}
