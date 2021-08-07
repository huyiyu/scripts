package nio.prepare.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NIOServer2 {


    private static Selector selector;

    /**
     * NIO Server服务器
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ServerSocketChannel nioServer = ServerSocketChannel.open();
        nioServer.bind(new InetSocketAddress(8888));
        nioServer.configureBlocking(false);
        selector = Selector.open();
        nioServer.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int select = selector.select(2);
            while (select != 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    SelectableChannel channel = selectionKey.channel();
                    if (selectionKey.isAcceptable()) {
                        // 设置不阻塞才可以使用 selector
                        ServerSocketChannel sc = (ServerSocketChannel) channel;
                        SocketChannel accept = sc.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }else {
                        SocketChannel sc = (SocketChannel) channel;
                        ByteBuffer allocate = ByteBuffer.allocate(5);
                        int len;
                        do {
                            len=sc.read(allocate);
                            System.out.print(new String(allocate.array(), 0, len));
                            allocate.clear();
                        } while (len != 0);
                        sc.write(ByteBuffer.wrap("OK".getBytes(StandardCharsets.UTF_8)));
                        sc.close();
                    }
                }
                selectionKeys.clear();
                select = 0;
            }
        }
    }
}
