package com.huyiyu.simple.sample.service.impl;


import com.huyiyu.excel.entity.RowResult;
import com.huyiyu.excel.service.AbstractImportService;
import com.huyiyu.simple.sample.entity.ShortEntity;
import com.huyiyu.simple.sample.entity.ShortEntityResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ShortImportService extends AbstractImportService<ShortEntity> {

    @Override
    public List<? extends RowResult> invoke(Map<Integer, ShortEntity> data) {
        List<ShortEntityResult> shortEntities  = new ArrayList<>();
        data.forEach((key,value)->{
            if (key % 2 == 0){
                ShortEntityResult shortEntityResult = new ShortEntityResult();
                shortEntityResult.setErrorMessage("不是奇数认为是错误的");
                shortEntityResult.setField10(value.getField10());
                shortEntityResult.setRowIndex(key);
                shortEntities.add(shortEntityResult);
            }
        });
        return shortEntities;
    }
}
