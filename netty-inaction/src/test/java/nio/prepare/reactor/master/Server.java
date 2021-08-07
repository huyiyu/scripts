package nio.prepare.reactor.master;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

/**
 * 单线程模型 单线程
 * 单 Selector ServerSocketChannel SocketChannel 共用
 */
public class Server {

    private Selector selector;
    private int bugCount;
    private static final int MAX_ERROR_COUNT = 10;

    public Server() {
    }

    public Server(int port) {
        try {
            ServerSocketChannel nioServer = ServerSocketChannel.open();
            nioServer.bind(new InetSocketAddress(port));
            nioServer.configureBlocking(false);
            selector = Selector.open();
            nioServer.register(selector, SelectionKey.OP_ACCEPT, new Acceptor(nioServer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("---------");
                if (safeSelect(0) == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    Acceptor attachment = (Acceptor) selectionKey.attachment();
                    attachment.accept();
                }
                selectionKeys.clear();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void executor(Runnable attachment) {
        attachment.run();
    }


    /**
     * 重建selector jdk bug 解决
     *
     * @param timeout 超时时间
     * @return select 个数
     * @throws IOException
     */
    public int safeSelect(long timeout) throws IOException {

        long start = System.nanoTime();
        int select = selector.select(timeout);
        long end = System.nanoTime();
        if (end - start < select) {
            System.out.println("可能发生空轮询,计数");
            bugCount++;
        }
        if (bugCount >= MAX_ERROR_COUNT) {
            bugCount = 0;
            Selector newSelector = Selector.open();
            for (SelectionKey key : selector.keys()) {
                key.cancel();
                key.channel().register(newSelector, key.interestOps());
            }
            selector.close();
            selector = newSelector;
        }
        return select;


    }
}
