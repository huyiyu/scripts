package com.cibidf.pbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cibidf.pbac.entity.HouseManagementAdmin;

/**
 * <p>
 * 房管局审核员 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
public interface IHouseManagementAdminService extends IService<HouseManagementAdmin> {

  boolean contains(Long accountId);
}
