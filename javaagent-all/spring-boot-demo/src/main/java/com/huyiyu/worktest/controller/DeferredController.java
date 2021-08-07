package com.huyiyu.worktest.controller;

import com.huyiyu.worktest.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
public class DeferredController {

    @Autowired
    private TestService testService;

    @GetMapping("preMain")
    public TestService preMain() {
        return testService;
    }

    @GetMapping("agentMain")
    public TestService agentMain() {
        return new TestService();
    }

}
