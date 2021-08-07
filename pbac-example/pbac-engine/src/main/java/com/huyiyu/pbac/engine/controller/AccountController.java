package com.huyiyu.pbac.engine.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huyiyu.pbac.engine.entity.Account;
import com.huyiyu.pbac.engine.result.R;
import com.huyiyu.pbac.engine.service.IAccountService;
import com.huyiyu.pbac.core.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 成交单 前端控制器
 * </p>
 *
 * @author huyiyu
 * @since 2024-07-31
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

  private final IAccountService accountService;
  private final JwtService jwtService;

  @RequestMapping("page")
  public R<Page<Account>> page() {
    return R.ok(accountService.page(Page.of(1, 10)));
  }

  @PostMapping("login")
  public R<String> login(String username,String password) {
    return R.ok(accountService.login(username,password));
  }
}
