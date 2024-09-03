package com.huyiyu.pbac.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.engine.entity.Account;

/**
 * <p>
 * 成交单 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
public interface IAccountService extends IService<Account> {

  String login(String username, String password);
}
