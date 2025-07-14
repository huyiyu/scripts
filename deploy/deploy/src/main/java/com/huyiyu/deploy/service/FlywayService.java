package com.huyiyu.deploy.service;

import com.huyiyu.deploy.dao.FlywayDao;
import com.huyiyu.deploy.property.DeployProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlywayService {

  private final DeployProperties deployProperties;
  private final FlywayDao flywayDao;

  public int baseline(String password) {
    return 0;
  }

  public int migration(String password) {

    return 0;
  }

  public int migrationVersion(String version, String password) {
    return 0;
  }

  public int repair(String password) {
    return 0;
  }

  public int rollback(String password) {
    return 0;
  }
}
