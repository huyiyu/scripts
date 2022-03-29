package nio.prepare.component.message;

import io.netty.channel.ChannelHandlerContext;
import nio.prepare.component.netty.UserChannelManager;
import nio.prepare.component.netty.handler.ClientMsgHandler;
import nio.prepare.enums.RequestTypeEnum;
import nio.prepare.pojo.WsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterMessageResolver implements MessageResolver{

    public static final Logger log = LoggerFactory.getLogger(RegisterMessageResolver.class);

    @Autowired
    private UserChannelManager userChannelManager;

    @Override
    public void resolve(WsRequest webSocketMsg, ChannelHandlerContext ctx) {
        log.debug("接收到用户登陆userId:{}",webSocketMsg.getUserId());
        userChannelManager.add(webSocketMsg.getUserId(),ctx.channel());


    }

    @Override
    public String getText() {
        return RequestTypeEnum.REGISTER.toString();
    }
}
