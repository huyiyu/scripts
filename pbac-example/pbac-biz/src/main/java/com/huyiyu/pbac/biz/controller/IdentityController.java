package com.huyiyu.pbac.biz.controller;

import com.huyiyu.pbac.biz.result.R;
import com.huyiyu.pbac.biz.service.ICustomerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("identity")
@RequiredArgsConstructor
public class IdentityController {

  private final ICustomerService customerService;

  @GetMapping
  public R<List<String>> identityListByAccountId(Long accountId) {
    Assert.notNull(accountId,"用户ID不能为空");
    return R.ok(customerService.identityListByAccountId(accountId));
  }


}
