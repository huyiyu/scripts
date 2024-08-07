package com.cibidf.pbac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cibidf.pbac.entity.HouseManagementAdmin;
import com.cibidf.pbac.mapper.HouseManagementAdminMapper;
import com.cibidf.pbac.service.IHouseManagementAdminService;
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
public class HouseManagementAdminServiceImpl extends ServiceImpl<HouseManagementAdminMapper, HouseManagementAdmin> implements IHouseManagementAdminService {

  @Override
  public boolean contains(Long accountId) {
    return lambdaQuery()
        .select(HouseManagementAdmin::getId)
        .eq(HouseManagementAdmin::getAccountId, accountId)
        .oneOpt()
        .isPresent();
  }
}
