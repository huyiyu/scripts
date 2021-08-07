package com.huyiyu.worktest.service;

import com.huyiyu.Replace;
import org.springframework.stereotype.Component;

@Replace
@Component
public class TestService {

    private String testField = "12345678";

    public String getTestField() {
        return testField;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }
}
