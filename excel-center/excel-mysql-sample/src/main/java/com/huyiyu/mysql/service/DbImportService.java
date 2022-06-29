package com.huyiyu.mysql.service;


import com.huyiyu.excel.entity.RowResult;
import com.huyiyu.excel.service.AbstractImportService;
import com.huyiyu.mysql.entity.DbEntity;
import com.huyiyu.mysql.entity.DbEntityResult;
import com.huyiyu.mysql.entity.DbTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DbImportService extends AbstractImportService<DbEntity> {

    @Resource
    private IDbTestService dbTestService;

    @Override
    public List<? extends RowResult> invoke(Map<Integer, DbEntity> data) {
        List<DbEntityResult> shortEntities = new ArrayList<>();
        List<DbTest> dbTests = new ArrayList<>();
        data.forEach((key, value) -> {
            if (key % 3 == 0) {
                DbEntityResult dbEntityResult = new DbEntityResult();
                dbEntityResult.setErrorMessage("不是奇数认为是错误的");
                dbEntityResult.setField10(value.getField10());
                dbEntityResult.setRowIndex(key);
                shortEntities.add(dbEntityResult);
            } else {
                DbTest dbTest = toDbTest(value);
                dbTests.add(dbTest);
            }
        });
        dbTestService.saveBatch(dbTests);
        return shortEntities;
    }

    private DbTest toDbTest(DbEntity value) {
        return new DbTest()
            .setField1(value.getField1())
            .setField2(value.getField2())
            .setField3(value.getField3())
            .setField4(value.getField4())
            .setField5(value.getField5())
            .setField6(value.getField6())
            .setField7(value.getField7())
            .setField8(value.getField8())
            .setField9(value.getField9())
            .setField10(value.getField10())
            .setField11(value.getField11())
            .setField12(value.getField12())
            .setField13(value.getField13())
            .setField14(value.getField14())
            .setField15(value.getField15())
            .setField16(value.getField16())
            .setField17(value.getField17())
            .setField18(value.getField18())
            .setField19(value.getField19())
            .setField20(value.getField20());
    }
}
