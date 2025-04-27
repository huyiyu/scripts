package com.cib.gpt.manager.exchange.service;

import com.cib.gpt.manager.exchange.req.CompletionsReq;
import com.cib.gpt.manager.exchange.resp.CompletionsResp;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface ChatExchange {

  @PostExchange(url = "/api/v1/chat/completions")
  CompletionsResp completions(@RequestBody CompletionsReq chatCompletionsReq);


  @GetExchange(url = "/api/core/chat/getHistories")
  Map getHistories();

}
