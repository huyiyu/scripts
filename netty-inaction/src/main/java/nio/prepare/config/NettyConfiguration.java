package nio.prepare.config;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import nio.prepare.component.netty.NettyServer;
import nio.prepare.component.netty.NettyProperties;
import nio.prepare.component.netty.UserChannelManager;
import nio.prepare.component.netty.handler.ClientMsgHandler;
import nio.prepare.component.netty.handler.HeartBeatHandler;
import nio.prepare.component.netty.handler.IPLimitHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableConfigurationProperties(value = NettyProperties.class)
public class NettyConfiguration {


    @Bean
    public NettyServer nettyServer(NettyProperties nettyProperties) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.setNettyProperties(nettyProperties);
        return nettyServer;
    }


}
