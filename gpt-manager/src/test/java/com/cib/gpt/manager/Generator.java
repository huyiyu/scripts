package com.cib.gpt.manager;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Generator {

  @Autowired
  private DataSourceProperties dataSourceProperties;

  @Test
  public void generator() {
    FastAutoGenerator
        .create(dataSourceProperties.getUrl(), dataSourceProperties.getUsername(),
            dataSourceProperties.getPassword())
        .globalConfig(builder -> builder
            .author("huyiyu")
            .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/test/java")
            .commentDate("yyyy-MM-dd")
        )
        .packageConfig(builder -> builder
            .parent("com.huyiyu.pbac.engine")
            .entity("entity")
            .mapper("mapper")
            .service("service")
            .serviceImpl("service.impl")
            .xml("mapper.xml")
            .controller("controller")
        )
        .strategyConfig(builder -> builder
            .entityBuilder()
            .enableLombok()
            .enableTableFieldAnnotation()
            .logicDeletePropertyName("deletedTime")
            .enableChainModel()
            .addTableFills(new Property("createTime", FieldFill.INSERT),
                new Property("updateTime", FieldFill.INSERT_UPDATE))
        )
        .templateEngine(new FreemarkerTemplateEngine())
        .execute();
  }


}
