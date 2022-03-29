package nio.prepare.component.netty;

import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ConfigurationProperties("netty")
public class NettyProperties {

    private static final Logger log = LoggerFactory.getLogger(NettyProperties.class);

    private String contextPath = "/ws";
    private int bossGroupCount = 1;
    private int workerGroupCount = Runtime.getRuntime().availableProcessors();
    private int port;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public int getBossGroupCount() {
        return bossGroupCount;
    }

    public void setBossGroupCount(int bossGroupCount) {
        this.bossGroupCount = bossGroupCount;
    }

    public int getWorkerGroupCount() {
        return workerGroupCount;
    }

    public void setWorkerGroupCount(int workerGroupCount) {
        this.workerGroupCount = workerGroupCount;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
