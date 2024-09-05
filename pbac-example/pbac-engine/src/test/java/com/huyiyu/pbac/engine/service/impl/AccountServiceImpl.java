package com.huyiyu.pbac.engine.service.impl;

import com.huyiyu.pbac.engine.entity.Account;
import com.huyiyu.pbac.engine.mapper.AccountMapper;
import com.huyiyu.pbac.engine.service.IAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 成交单 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-05
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {

}
