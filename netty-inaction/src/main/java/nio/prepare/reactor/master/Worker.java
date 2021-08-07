package nio.prepare.reactor.master;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Worker {


    private SocketChannel socketChannel;

    public Worker(SocketChannel accept) {
        this.socketChannel = accept;
    }

    public void process() {
        ByteBuffer allocate = ByteBuffer.allocate(15);
        int len;
        if (socketChannel.isOpen()) {
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
}
