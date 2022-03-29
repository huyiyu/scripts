package nio.prepare.component.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import nio.prepare.component.message.MessageResolver;
import nio.prepare.component.netty.UserChannelManager;
import nio.prepare.enums.RequestTypeEnum;
import nio.prepare.pojo.WsRequest;
import nio.prepare.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author wenjie
 */

@ChannelHandler.Sharable
@Component
public class ClientMsgHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static final Logger log = LoggerFactory.getLogger(ClientMsgHandler.class);
    @Autowired
    private UserChannelManager userChannelManager;

    private Map<String, MessageResolver> strategy;

    @Autowired
    public void setMessageResolver(List<MessageResolver> mr) {
        strategy = mr.stream().collect(Collectors.toMap(MessageResolver::getText, Function.identity()));
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
            throws Exception {
        String json = msg.text();
        WsRequest webSocketMsg = JsonUtils.jsonToObject(json, WsRequest.class);
        MessageResolver messageResolver = strategy.computeIfPresent(webSocketMsg.getType(), (k, v) -> {
            v.resolve(webSocketMsg, ctx);
            return v;
        });
        if (messageResolver == null) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":-1,\"message\":\"type invalid\"}"));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userChannelManager.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":-1,\"message\":\"exception cause\"}"));
        cause.printStackTrace();
    }
}