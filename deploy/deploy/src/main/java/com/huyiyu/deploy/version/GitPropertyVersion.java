package com.huyiyu.deploy.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import picocli.CommandLine.IVersionProvider;

public class GitPropertyVersion implements IVersionProvider {

  private static String[] version;

  static {
    try (InputStream resourceAsStream = GitPropertyVersion.class.getClassLoader()
        .getResourceAsStream("git.properties")) {
      Properties properties = new Properties();
      properties.load(resourceAsStream);
      version = new String[]{
          "提交信息: " + properties.getProperty("git.commit.message.short"),
          "版本号: " + properties.getProperty("git.build.version"),
          "作者: " + properties.getProperty("git.commit.user.name"),
          "发布时间: " + properties.getProperty("git.commit.time")
      };


    } catch (IOException e) {
      throw new RuntimeException("加载git版本出错", e);
    }
  }


  @Override
  public String[] getVersion() {
    return version;
  }
}
