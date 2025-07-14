package com.huyiyu.deploy;

import com.huyiyu.deploy.command.RootCommand;
import com.huyiyu.deploy.property.DeployProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import picocli.CommandLine;

@SpringBootApplication
@RequiredArgsConstructor
public class DeployApplication implements CommandLineRunner, ExitCodeGenerator {

  private final RootCommand rootCommand;
  private int exitcode;


  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(DeployApplication.class, args)));
  }


  @Override
  public void run(String... args) throws Exception {
    this.exitcode = new CommandLine(rootCommand).execute(args);
  }

  @Override
  public int getExitCode() {
    return this.exitcode;
  }
}
