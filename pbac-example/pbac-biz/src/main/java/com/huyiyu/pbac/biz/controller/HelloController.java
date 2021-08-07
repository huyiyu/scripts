package com.huyiyu.pbac.biz.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello pbac-biz!";
  }


  @GetMapping("hello2")
  public Map<String, Object> hello2() {
    return Map.of("1","abcdef","2","efawefawegawefawefawegbawegawesrawe");
  }

}
