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
      version = properties.values().toArray(new String[0]);
    } catch (IOException e) {
      throw new RuntimeException("加载git版本出错", e);
    }
  }


  @Override
  public String[] getVersion() {
    return version;
  }
}
