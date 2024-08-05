package com.cibidf.pbac.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cibidf.pbac.entity.Bill;
import com.cibidf.pbac.result.R;
import com.cibidf.pbac.service.IBillService;
import com.cibidf.pbac.service.impl.BillServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 成交单 前端控制器
 * </p>
 *
 * @author huyiyu
 * @since 2024-07-31
 */
@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillController {

  private final IBillService billService;


  @GetMapping("page")
  public R<Page<Bill>> page() {
    return R.ok(billService.page(Page.of(1, 10)));
  }

  @GetMapping("getById/{id}")
  public R<Bill> getById(@PathVariable("id") Long id) {
    Assert.notNull(id,"id不能为空");
    return R.ok(billService.getById(id));
  }

}
