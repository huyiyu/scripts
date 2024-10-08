package com.huyiyu.pbac.engine.controller;

import com.huyiyu.pbac.core.domain.PbacRuleResult;
import com.huyiyu.pbac.engine.result.R;
import com.huyiyu.pbac.engine.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 请求客户端资源 前端控制器
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {

  @Autowired
  private IResourceService resourceService;

  @GetMapping("getRuleResultByPattern")
  public R<PbacRuleResult> getRuleResultByPattern(String pattern) {
    Assert.hasText(pattern, "pattern must not be empty");
    return R.ok(resourceService.getRuleResultByPattern(pattern));
  }

}
