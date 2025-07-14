package com.huyiyu.deploy.utils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

  public static void unpack(InputStream inputStream, File file) {
    try (ZipInputStream zis = new ZipInputStream(inputStream)) {
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        Path newPath = zipSlipProtect(zipEntry, Paths.get(file.getAbsolutePath()));
        if (zipEntry.isDirectory()) {
          Files.createDirectories(newPath);
        } else {
          if (newPath.getParent() != null) {
            if (Files.notExists(newPath.getParent())) {
              Files.createDirectories(newPath.getParent());
            }
          }
          Files.copy(zis, newPath);
        }
        zipEntry = zis.getNextEntry();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) {
    Path targetDirResolved = targetDir.resolve(zipEntry.getName());
    Path normalizePath = targetDirResolved.normalize();
    if (!normalizePath.startsWith(targetDir)) {
      throw new RuntimeException("恶意 ZIP 文件: " + zipEntry.getName());
    }
    return normalizePath;
  }
}
