package com.huyiyu.simple.service.impl;


import com.huyiyu.excel.entity.ImportConfig;
import com.huyiyu.excel.service.ImportConfigService;
import com.huyiyu.simple.enums.SimpleImportConfig;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleImportConfigServiceImpl implements ImportConfigService {

    private static final Map<String, ImportConfig> map =
        Arrays.stream(SimpleImportConfig.values())
        .collect(Collectors.toMap(SimpleImportConfig::getType, Function.identity()));


    @Override
    public ImportConfig getConfig(String simpleName) {
        return map.get(simpleName);
    }
}
