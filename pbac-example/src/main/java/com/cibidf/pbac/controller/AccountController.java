package com.cibidf.pbac.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cibidf.pbac.entity.Account;
import com.cibidf.pbac.result.R;
import com.cibidf.pbac.service.IAccountService;
import lombok.RequiredArgsConstructor;
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

  @RequestMapping("page")
  public R<Page<Account>> page() {
    return R.ok(accountService.page(Page.of(1, 10)));
  }

}
