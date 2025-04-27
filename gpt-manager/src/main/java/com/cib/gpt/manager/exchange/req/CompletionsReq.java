package com.cib.gpt.manager.exchange.req;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CompletionsReq {

  private String chatId;
  private final boolean stream = false;
  private Boolean detail;
  private String responseChatItemId;
  private Variables variables;
  private List<Message> messages;

  @Data
  @Accessors(chain = true)
  public static class Variables {

    private String uid;
    private String name;

  }

  @Data
  @Accessors(chain = true)
  public static class Message {

    private String content;
    private String role;

  }
}
