package com.cib.gpt.manager;

import com.cib.gpt.manager.exchange.req.HistoriesReq;
import com.cib.gpt.manager.exchange.service.ChatExchange;
import com.cib.gpt.manager.exchange.req.CompletionsReq;
import com.cib.gpt.manager.property.AppProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@RestController
public class GptManagerApplication {

  @Resource
  private ChatExchange chatExchange;

  @GetMapping("test")
  public Object getMapping(){
    return chatExchange.getHistories();
  }

  public static void main(String[] args) {
    SpringApplication.run(GptManagerApplication.class, args);
  }
}
