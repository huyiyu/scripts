package com.huyiyu.deploy.command;

import com.huyiyu.deploy.service.FlywayService;
import com.huyiyu.deploy.version.GitPropertyVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "eco-deploy", description = "辅助部署工具", versionProvider = GitPropertyVersion.class)
@Component
@Slf4j
@RequiredArgsConstructor
public class RootCommand {

  private final FlywayService flywayService;

  @Option(names = {"-v", "--version"}, versionHelp = true, description = "版本信息")
  private boolean version;
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "帮助信息")
  private boolean help;

  @Command(name = "baseline", description = "数据库基线版本初始化")
  private int baseline(
      @Option(names = {"-h", "--help"}, usageHelp = true, description = "帮助信息") boolean help,
      @Option(names = {"-p",
          "--password"}, description = "数据库密码", interactive = true) String password) {
    return flywayService.baseline(password);
  }

  @Command(name = "migration", description = "数据库更新")
  private int migration(
      @Option(names = {"-h", "--help"}, usageHelp = true, description = "帮助信息") boolean help,
      @Option(names = {"-p",
          "--password"}, description = "数据库密码", interactive = true) String password) {
    return flywayService.migration(password);
  }

  @Command(name = "repair", description = "清除错误执行记录")
  private int repair(@Option(names = {"-p",
      "--password"}, description = "数据库密码", interactive = true) String password) {
    return flywayService.repair(password);
  }

  @Command(name = "rollback", description = "数据库基线版本初始化")
  private int rollback(
      @Option(names = {"-h", "--help"}, usageHelp = true, description = "帮助信息") boolean help,
      @Option(names = {"-p",
          "--password"}, description = "数据库密码", interactive = true) String password) {
    return flywayService.rollback(password);
  }

  @Command(name = "migration-version", description = "数据库回滚到上个版本")
  private int migrationVersion(
      @Option(names = {"-h", "--help"}, usageHelp = true, description = "帮助信息") boolean help,
      @Option(names = {"-p",
          "--password"}, description = "数据库密码", interactive = true) String password,
      @Option(names = {"-v",
          "--version"}, description = "版本号", paramLabel = "[VERSION]",required = true) String version
  ) {
    return flywayService.migrationVersion(version, password);
  }

}
