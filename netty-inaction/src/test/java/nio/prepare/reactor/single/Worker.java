package nio.prepare.reactor.single;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Worker implements Runnable {
    private SocketChannel socketChannel;
    private Selector selector;

    public Worker(SocketChannel accept, Selector selector) {
        this.socketChannel = accept;
        this.selector = selector;
    }

    @Override
    public void run() {
        ByteBuffer allocate = ByteBuffer.allocate(15);
        int len;
        try {
            do {
                len = socketChannel.read(allocate);
                System.out.print(new String(allocate.array(), 0, len));
                allocate.clear();
            } while (len != 0);
            String ok =  "HTTP/1.1 200 OK \nContent-Type: text/html;Charset=utf-8\n\r\nOK";
            socketChannel.write(ByteBuffer.wrap(ok.getBytes(StandardCharsets.UTF_8)));
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
