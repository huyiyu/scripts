package com.cibidf.huyiyu;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "tail", header = StringConst.HEADER, version = {"v1.0"}, description = {
    StringConst.DESC})
public class TailMain implements Callable<Integer> {

  @Parameters(hidden = true, defaultValue = StringConst.EMPTY)
  private String path;
  @Option(names = "-f", hidden = true)
  private boolean follow;
  @Option(names = "-n", defaultValue = "10", hidden = true)
  private Integer line;


  public static void main(String[] args) {
    System.exit(new CommandLine(new TailMain()).execute(args));
  }

  @Override
  public Integer call() throws Exception {
    String rootFilePath = getRootFilePath();
    if (rootFilePath.length() == 0) {
      System.out.println(StringConst.HEADER);
      System.out.println(StringConst.DESC);
      return 0;
    }
    long count = Files.lines(Path.of(rootFilePath)).count();
    Files.lines(Path.of(rootFilePath))
        .skip(count - line)
        .forEach(System.out::println);
    if (follow) {
      try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(rootFilePath))) {
        lineNumberReader.skip(Files.size(Path.of(rootFilePath)));
        while (true) {
          String line = lineNumberReader.readLine();
          if (line != null) {
            System.out.println(line);
          } else {
            TimeUnit.SECONDS.sleep(1);
          }
        }
      }
    }
    return 0;
  }

  private String getRootFilePath() {
    if (path == null || path.strip().isEmpty()) {
      return StringConst.EMPTY;
    }
    File file = new File(path);
    if (file.exists()) {
      return file.getAbsolutePath();
    }
    file = new File(System.getProperty("user.dir"), path);
    if (file.exists()) {
      return file.getAbsolutePath();
    }
    return StringConst.EMPTY;
  }
}
