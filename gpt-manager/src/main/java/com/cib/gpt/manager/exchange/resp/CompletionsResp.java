package com.cib.gpt.manager.exchange.resp;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CompletionsResp {

  private List<ChatResponse> responseData;
  private List<Choice> choices;
  private String id;
  private String model;
  private String usage;

  @Data
  public static class ChatResponse {

    private String moduleName;
    private BigDecimal price;
    private String question;
    private String answer;
    private String maxToken;
    private String model;
    private Integer tokens;
    private BigDecimal similarity;
    private Integer limit;
    private List<Quote> quoteList;
    private List<CompleteMessage> completeMessages;

  }

  @Data
  public static class Quote {

    private String value;
    private String dataSetId;
    private String id;
    private String q;
    private String a;
    private String source;


  }

  @Data
  public static class CompleteMessage {

    private String object;
    private String value;

  }

  @Data
  public static class Choice {

    private Message message;
    private String finishReason;
    private Integer index;
  }

  @Data
  public static class Message {

    private String role;
    private String content;
  }
}
