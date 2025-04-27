package com.cib.gpt.manager.service.exchange;

import com.cib.gpt.manager.exchange.service.ChatExchange;
import com.cib.gpt.manager.exchange.req.CompletionsReq;
import com.cib.gpt.manager.exchange.resp.CompletionsResp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatExchangeTest {

  @Resource
  private ChatExchange chatExchange;

  @Test
  public void testCompletions() {
    CompletionsResp completions = chatExchange.completions(new CompletionsReq());
  }

}