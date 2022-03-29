package nio.prepare.component.message;

import io.netty.channel.ChannelHandlerContext;
import nio.prepare.enums.RequestTypeEnum;
import nio.prepare.pojo.WsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KeepAliveMessageResolver implements MessageResolver{

    private static final Logger log = LoggerFactory.getLogger(KeepAliveMessageResolver.class);

    @Override
    public void resolve(WsRequest webSocketMsg, ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("接收到客户端:{} 心跳信息",webSocketMsg.getUserId());
        }
    }

    @Override
    public String getText() {
        return RequestTypeEnum.KEEPALIVE.toString();
    }
}
