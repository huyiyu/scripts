package com.huyiyu.deploy.constant;

import java.util.regex.Pattern;

public interface DbConstant {

  String FILE_SYSTEM_PREFIX = "filesystem:";
  Pattern VERSION_PATTERN = Pattern.compile("(<?version>\\d+\\.\\d+\\.\\d+)\\.\\d+");
  /**
   * 查询初始化表是否存在
   */
  String CHECK_EXIST_SQL = """
      SELECT 
        COUNT(*) as count
      FROM
        information_schema.`tables` t
      WHERE
       t.TABLE_SCHEMA='flywaydb' 
      AND
       t.TABLE_NAME = 'flyway_schema_history'
      """;
  /**
   *
   */
  String GET_PREVIOUS_VERSION_SQL = """
       SELECT 
         version  
       FROM 
         flywaydb.flyway_schema_history
       WHERE
       ORDER BY 
         zinstalled_rank DESC
       LIMIT 1
      """;

}
