package nio.prepare.component.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import nio.prepare.component.netty.UserChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@ChannelHandler.Sharable
@Component
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private UserChannelManager userChannelManager;

    private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.READER_IDLE) {
                log.debug("READER_IDLE...");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.debug("WRITER_IDLE...");
            } else if (event.state() == IdleState.ALL_IDLE) {
                // Closes the channel which state is ALL_IDLE
                Channel channel = ctx.channel();
                // Clear cache
                userChannelManager.remove(channel);
                channel.close();
                log.debug("close channel: {}", channel.id().asLongText());
            }
        }

    }

    public UserChannelManager getUserChannelManager() {
        return userChannelManager;
    }

    public void setUserChannelManager(UserChannelManager userChannelManager) {
        this.userChannelManager = userChannelManager;
    }
}
