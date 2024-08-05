package com.cibidf.pbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cibidf.pbac.entity.AccountRole;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * <p>
 * 房管局审核员 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
public interface IAccountRoleService extends IService<AccountRole> {

  List<String> listNamesByAccountId(Long id);
}
