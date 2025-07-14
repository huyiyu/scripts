package com.huyiyu.deploy.dao;

import com.huyiyu.deploy.property.DeployProperties;
import com.huyiyu.deploy.property.DeployProperties.Flyway;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public abstract class AbstractJdbcTemplateDaoSupport implements BeanFactoryAware {

  private Flyway flyway;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    DeployProperties bean = beanFactory.getBean(DeployProperties.class);
    this.flyway = bean.getFlyway();
  }

  @FunctionalInterface
  protected static interface DaoFunction<T>  {
    T run(JdbcTemplate jdbcTemplate) throws SQLException;
  }

  protected <T> T smartExecuteSql(String password, DaoFunction<T> function) {
    password = password == null ? flyway.getPassword() : password;
    try (Connection connection = DriverManager.getConnection(flyway.getJdbcUrl(),flyway.getUsername(),password)) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(connection, Types.VARCHAR);
      return function.run(jdbcTemplate);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void execute(String password, String sql) {
    smartExecuteSql(password,jdbcTemplate -> jdbcTemplate.executeStatement(sql));
  }
}
