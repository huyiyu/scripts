package com.huyiyu.pbac.engine.controller;

import com.huyiyu.pbac.engine.result.R;
import com.huyiyu.pbac.engine.service.IRoleResourceService;
import com.huyiyu.pbac.engine.service.IRoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 房管局审核员 前端控制器
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-01
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

  private final IRoleResourceService roleResourceService;

  @GetMapping("roleCodesByResourceId")
  public R<List<String>> roleCodesByResourceId(Long id){
    List<String> data = roleResourceService.roleCodesByResourceId(id);
    return R.ok(data);
  }

}
