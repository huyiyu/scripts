package com.huyiyu.pbac.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.biz.entity.HouseManagementAdmin;
import com.huyiyu.pbac.biz.mapper.HouseManagementAdminMapper;
import com.huyiyu.pbac.biz.service.IHouseManagementAdminService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房管局审核员 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
@Service
public class HouseManagementAdminServiceImpl extends ServiceImpl<HouseManagementAdminMapper, HouseManagementAdmin> implements
    IHouseManagementAdminService {

  @Override
  public boolean isHouseManager(Long accountId) {
    return lambdaQuery()
        .eq(HouseManagementAdmin::getAccountId, accountId)
        .exists();
  }
}
