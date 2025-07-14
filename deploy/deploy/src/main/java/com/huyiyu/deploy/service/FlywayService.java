package com.huyiyu.deploy.service;

import static com.huyiyu.deploy.constant.DbConstant.FILE_SYSTEM_PREFIX;
import static com.huyiyu.deploy.constant.DbConstant.VERSION_PATTERN;

import com.huyiyu.deploy.dao.FlywayDao;
import com.huyiyu.deploy.http.ArtifactoryExchange;
import com.huyiyu.deploy.property.DeployProperties;
import com.huyiyu.deploy.utils.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.util.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlywayService {

  private final DeployProperties deployProperties;
  private final FlywayDao flywayDao;
  private final ArtifactoryExchange artifactoryExchange;


  public int baseline(String password) {
    if (!flywayDao.checkTableExist(password)) {
      log.warn("正在初始化基线版本");
      Flyway flyway = createFlyway(password, deployProperties.getFlyway().getLocations());
      return flyway.baseline().successfullyBaselined ? 0 : 1;
    }
    log.warn("无需初始化基线版本");
    return 0;
  }

  public int migration(String password) {
    if (!flywayDao.checkTableExist(password)) {
      log.error("请先初始化基线版本");
      return 0;
    }
    Flyway flyway = createFlyway(password, deployProperties.getFlyway().getLocations());
    return flyway.migrate().success ? 0 : 1;
  }

  public int migrationVersion(String version, String password) {
    if (flywayDao.checkTableExist(password)) {
      log.error("请先初始化基线版本");
      return 0;
    } else {
      return downZipFileByVersionAndDelete(version, path -> {
        Flyway flyway = createFlyway(password, FILE_SYSTEM_PREFIX + path.getAbsolutePath());
        return flyway.migrate().success ? 0 : 1;
      });
    }
  }


  public int repair(String password) {
    if (flywayDao.checkTableExist(password)) {
      Flyway flyway = createFlyway(password, deployProperties.getFlyway().getLocations());
      flyway.repair();
    } else {
      log.error("请先初始化基线版本");
    }
    return 0;
  }

  public int rollback(String password) {
    if (flywayDao.checkTableExist(password)) {
      String rawVersion = flywayDao.getPreviousVersion(password);
      if (!StringUtils.hasText(rawVersion)) {
        throw new RuntimeException("不支持回滚,可能的原因是: 没有可回滚的版本？超过回滚时效");
      }
      Matcher matcher = VERSION_PATTERN.matcher(rawVersion);
      if (!matcher.matches()) {
        throw new RuntimeException("版本号不正确");
      }
      String version = matcher.group("version");
      downZipFileByVersionAndDelete(version, path -> {
        File parentFile = new File(path, "db/rollback");
        for (File file : parentFile.listFiles()) {
          String sql = FileUtils.readAsString(Paths.get(file.getAbsolutePath()));
          flywayDao.execute(password, sql);
        }
        return 0;
      });
    } else {
      log.error("请先初始化基线版本");
    }
    return 0;
  }

  private Flyway createFlyway(String password, String locations) {
    DeployProperties.Flyway flyway = deployProperties.getFlyway();
    locations = StringUtils.hasText(locations) ? locations : flyway.getLocations();
    password = StringUtils.hasText(password) ? password : flyway.getPassword();
    return org.flywaydb.core.Flyway.configure()
        .dataSource(flyway.getJdbcUrl(), flyway.getUsername(), password)
        .baselineVersion(flyway.getBaselineVersion())
        .baselineOnMigrate(flyway.isBaselineOnMigrate())
        .sqlMigrationPrefix(flyway.getMirgatePrefix())
        .sqlMigrationSeparator(flyway.getMigrateSeparator())
        .sqlMigrationSuffixes(flyway.getMigrateSuffix())
        .defaultSchema(flyway.getSchema())
        .encoding(StandardCharsets.UTF_8)
        .locations(locations)
        .load();
  }


  private int downZipFileByVersionAndDelete(String version, Function<File, Integer> function) {
    String templatFileName = UUID.randomUUID().toString() + ".zip";
    File file = new File(deployProperties.getRepo().getWorkdir(), templatFileName);
    Resource resource = artifactoryExchange.pull(version + ".zip");
    try (InputStream inputStream = resource.getInputStream()) {
      ZipUtil.unpack(inputStream, file);
      return function.apply(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (file.exists() && file.isDirectory()) {
        file.delete();
      }
    }
  }
}
