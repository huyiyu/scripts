package com.huyiyu.pbac.engine.convert;

import com.huyiyu.pbac.engine.entity.Account;
import com.huyiyu.pbac.core.domain.LoginUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountConvert {

  AccountConvert INSTANCE = Mappers.getMapper(AccountConvert.class);

  LoginUser account2LoginUser(Account account);
}
