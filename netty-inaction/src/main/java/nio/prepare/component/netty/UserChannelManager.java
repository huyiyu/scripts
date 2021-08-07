package nio.prepare.component.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import nio.prepare.pojo.WsRequest;
import nio.prepare.pojo.WsResponse;
import nio.prepare.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class UserChannelManager {

    private static final Logger log = LoggerFactory.getLogger(UserChannelManager.class);

    private ConcurrentHashMap<Integer, ChannelGroup> map = new ConcurrentHashMap<>();

    @Autowired
    private Environment environment;

    @KafkaListener(topics = "message")
    public void kafkaListen(String data) {
        String serverName = environment.getProperty("spring.application.name");
        log.info("{} 接收到kafka消息:{}", serverName, data);
        WsResponse wsResponse = JsonUtils.jsonToObject(data, WsResponse.class);
        Integer userId = wsResponse.getUserId();
        Set<Channel> channels = map.get(userId);
        if (!CollectionUtils.isEmpty(channels)) {
            for (Channel channel : channels) {
                if (channel.isOpen()) {
                    wsResponse.setServer(serverName);
                    data = JsonUtils.toJsonString(wsResponse);
                    channel.writeAndFlush(new TextWebSocketFrame(data));
                }
            }
        }
    }

    public void add(Integer userId, Channel channel) {
        Set<Channel> channels = map.compute(userId, (k, v) -> {
            if (v == null){
                v = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            }
            v.add(channel);
            return v;
        });

    }

    /**
     * Remove the element by uid
     *
     * @param uid uid
     */
    public void remove(@NonNull Integer uid) {
        map.remove(uid);
    }

    /**
     * Remove the cache by channel
     *
     * @param channel channel
     */
    public void remove(@NonNull Channel channel) {
        map.entrySet().stream().filter(entry -> entry.getValue().contains(channel))
                .forEach(entry -> entry.getValue().remove(channel));
    }

    /**
     * Get channel by uid
     *
     * @param uid uid
     * @return channel
     */
    @Nullable
    public Set<Channel> get(@NonNull Integer uid) {
        return map.get(uid);
    }

    /**
     * Clear cache
     */
    public void clearAll() {
        map.clear();
    }


}
