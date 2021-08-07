package nio.prepare.reactor.muitiple;


import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Accceptor implements Runnable {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;


    public Accceptor(ServerSocketChannel channel, Selector selector) {
        this.serverSocketChannel = channel;
        this.selector = selector;
    }

    @Override
    public void run() {
        // 设置不阻塞才可以使用 selector
        try {
            SocketChannel accept = serverSocketChannel.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ,new Worker(accept,selector));
            selector.wakeup();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
}
