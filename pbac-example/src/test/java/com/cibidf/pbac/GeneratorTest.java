package com.cibidf.pbac;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;
import jakarta.annotation.Resource;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GeneratorTest {

  @Resource
  private DataSourceProperties dataSourceProperties;

  @Test
  public void generate(){
    FastAutoGenerator.create(dataSourceProperties.getUrl(), dataSourceProperties.getUsername(), dataSourceProperties.getPassword())
        .globalConfig(builder -> builder
            .author("huyiyu")
            .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/test/java")
            .commentDate("yyyy-MM-dd")
        )
        .packageConfig(builder -> builder
            .parent("com.cibidf.pbac")
            .entity("entity")
            .mapper("mapper")
            .service("service")
            .serviceImpl("service.impl")
            .controller("controller")
            .xml("mapper.xml")
        )
        .strategyConfig(builder -> builder
            .entityBuilder()
            .enableLombok()
            .enableChainModel()
            .enableRemoveIsPrefix()
            .enableTableFieldAnnotation()
            .addTableFills(List.of(
                new Property("createTime", FieldFill.INSERT),
                new Property("updateTime", FieldFill.INSERT_UPDATE)
            )).logicDeleteColumnName("deletedTime")
        )
        .templateEngine(new FreemarkerTemplateEngine())
        .execute();
  }



}
