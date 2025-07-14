package com.huyiyu.deploy.dao;

import static com.huyiyu.deploy.constant.DbConstant.CHECK_EXIST_SQL;
import static com.huyiyu.deploy.constant.DbConstant.GET_PREVIOUS_VERSION_SQL;

import org.springframework.stereotype.Repository;

@Repository
public class FlywayDao extends AbstractJdbcTemplateDaoSupport {


  public boolean checkTableExist(String password) {
    return smartExecuteSql(password, jdbcTemplate -> jdbcTemplate.queryForLong(CHECK_EXIST_SQL) > 0L);
  }

  public String getPreviousVersion(String password) {
    return smartExecuteSql(password, jdbcTemplate -> jdbcTemplate.queryForString(GET_PREVIOUS_VERSION_SQL));
  }
}
