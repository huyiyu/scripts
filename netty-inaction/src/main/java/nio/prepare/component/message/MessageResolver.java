package nio.prepare.component.message;

import io.netty.channel.ChannelHandlerContext;
import nio.prepare.pojo.WsRequest;

public interface MessageResolver {

    void resolve(WsRequest webSocketMsg, ChannelHandlerContext ctx);

    String getText();
}
