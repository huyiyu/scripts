package com.cib.gpt.manager.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;



@ConfigurationProperties("app")
public class AppProperties {

  private Fastgpt fastgpt = new Fastgpt();

  public Fastgpt getFastgpt() {
    return fastgpt;
  }

  public void setFastgpt(Fastgpt fastgpt) {
    this.fastgpt = fastgpt;
  }

  public static class Fastgpt {

    private String baseurl;
    private String apikey;

    public String getBaseurl() {
      return baseurl;
    }

    public void setBaseurl(String baseurl) {
      this.baseurl = baseurl;
    }

    public String getApikey() {
      return apikey;
    }

    public void setApikey(String apikey) {
      this.apikey = apikey;
    }
  }
}
