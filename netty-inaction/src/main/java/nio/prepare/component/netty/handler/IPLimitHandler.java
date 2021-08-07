package nio.prepare.component.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import nio.prepare.component.netty.UserChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class IPLimitHandler extends ChannelInboundHandlerAdapter {

}
