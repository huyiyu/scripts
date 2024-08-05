package com.cibidf.pbac;

import cn.hutool.crypto.SmUtil;
import com.cibidf.pbac.entity.Account;
import com.cibidf.pbac.service.IAccountService;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountMapperTest {

  @Resource
  private IAccountService accountService;

  @Test
  public void updatePassword() {
    accountService.getBaseMapper().selectList(null, resultHandler -> {
      Account resultObject = resultHandler.getResultObject();
      accountService.lambdaUpdate()
          .set(Account::getPassword, SmUtil.sm3WithSalt(resultObject.getUsername().getBytes(
              StandardCharsets.UTF_8)).digestHex("123456"))
          .eq(Account::getId,resultObject.getId()).update();
    });
  }

}
