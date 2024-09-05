package com.huyiyu.pbac.engine;

import cn.hutool.crypto.SmUtil;
import com.huyiyu.pbac.engine.entity.Account;
import com.huyiyu.pbac.engine.service.IAccountService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountTest {

  @Autowired
  private IAccountService accountService;

  @Test
  public void accountService() {

    List<Account> list = accountService
        .lambdaQuery()
        .select(Account::getId, Account::getUsername)
        .list();
    list.forEach(account ->
        account.setPassword(
            SmUtil.sm3WithSalt(account.getUsername().getBytes(StandardCharsets.UTF_8))
                .digestHex("123456")
        )
    );
    accountService.updateBatchById(list);
  }

}
