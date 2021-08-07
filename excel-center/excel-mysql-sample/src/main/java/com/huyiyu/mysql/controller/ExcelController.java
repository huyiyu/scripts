package com.huyiyu.mysql.controller;

import com.huyiyu.mysql.service.DbImportService;
import java.io.IOException;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ExcelController {

    @Resource
    private DbImportService service;

    @PostMapping("excelUpload")
    public Object upload(MultipartFile file) throws IOException {
        long integer = service.doRead(file.getInputStream());
        // 实际通过判断ID的返回来确定任务是否执行成功
        return integer;
    }
}
