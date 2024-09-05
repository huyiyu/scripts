package com.huyiyu.pbac.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.engine.entity.AccountRole;
import java.util.List;

/**
 * <p>
 * 房管局审核员 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
public interface IAccountRoleService extends IService<AccountRole> {

  List<String> listRoleCodesByAccountId(long accountId);
}
