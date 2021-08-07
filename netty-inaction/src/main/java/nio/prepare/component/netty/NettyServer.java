package nio.prepare.component.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import nio.prepare.component.netty.handler.ClientMsgHandler;
import nio.prepare.component.netty.handler.HeartBeatHandler;
import nio.prepare.component.netty.handler.IPLimitHandler;
import nio.prepare.component.netty.handler.WebSocketIndexPageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyServer extends ChannelInitializer<Channel> implements SmartInitializingSingleton, BeanFactoryAware {

    public static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private NettyProperties nettyProperties;

    private ServerBootstrap serverBootstrap;

    private BeanFactory beanFactory;

    public void setNettyProperties(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(beanFactory.getBean(IPLimitHandler.class))
                .addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(1024 * 64))
                .addLast(new IdleStateHandler(10, 10, 30, TimeUnit.MINUTES))
                .addLast(beanFactory.getBean(HeartBeatHandler.class))
                .addLast(new WebSocketServerProtocolHandler(nettyProperties.getContextPath()))
                .addLast(beanFactory.getBean(WebSocketIndexPageHandler.class))
                .addLast(beanFactory.getBean(ClientMsgHandler.class));

    }

    @Override
    @Async
    public void afterSingletonsInstantiated() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(nettyProperties.getBossGroupCount());
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(nettyProperties.getWorkerGroupCount());
        serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture sync = serverBootstrap.bind(nettyProperties.getPort()).sync();
            sync.addListener(channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("WebSocketServer - Started on port: {}", nettyProperties.getPort());
                } else {
                    throw new RuntimeException("WebSocket启动失败！");
                }
            });
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
