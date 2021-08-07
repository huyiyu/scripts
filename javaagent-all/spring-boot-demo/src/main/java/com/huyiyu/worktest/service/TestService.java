package com.huyiyu.worktest.service;

import org.springframework.stereotype.Component;

@Component
public class TestService {

    private String testField = "";

    public String getTestField() {
        return testField;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }
}
