package com.huyiyu.simple.sample.controller;

import com.huyiyu.simple.sample.service.impl.ShortImportService;
import java.io.IOException;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ExcelController {

    @Resource
    private ShortImportService service;

    @PostMapping("excelUpload")
    public Object upload(MultipartFile file) throws IOException {
        long integer = service.doRead(file.getInputStream());
        // 实际通过判断ID的返回来确定任务是否执行成功
        return integer;
    }
}
