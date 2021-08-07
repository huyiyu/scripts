package com.huyiyu.pbac.core.property;

import java.time.Duration;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("pbac")
public class PbacProperties {

  private Jwt jwt = new Jwt();
  private String[] permitAllPattern;

  @Data
  public static class Jwt {

    private String secret;
    private Duration expiration = Duration.ofHours(1);
    private String issuer = "http://pbac.huyiyu.com";
    private List<String> adience = List.of("web");
  }

}
