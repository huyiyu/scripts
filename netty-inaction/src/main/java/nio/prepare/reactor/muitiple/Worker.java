package nio.prepare.reactor.muitiple;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Worker implements Runnable {

    private static final int THREAD_COUNTING = 10;

    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(THREAD_COUNTING, THREAD_COUNTING, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());


    private SocketChannel socketChannel;
    private Selector selector;

    public Worker(SocketChannel accept, Selector selector) {
        this.socketChannel = accept;
        this.selector = selector;
    }

    @Override
    public void run() {
        // 单selector 多线程模型中不允许把读请求放到多线程中,因为select 监听了read 事件 当channel就绪了
        // 便会重新触发读事件导致多线程共同处理一个 channel
        int len;
        try {
            ByteBuffer allocate = ByteBuffer.allocate(15);
            do {
                len = socketChannel.read(allocate);
                System.out.print(new String(allocate.array(), 0, len));
                allocate.clear();
            } while (len != 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        threadPoolExecutor.execute(this::process);


    }

    private void process() {
        ByteBuffer allocate = ByteBuffer.allocate(15);
        int len;
        try {
            String ok = "HTTP/1.1 200 OK \nContent-Type: text/html;Charset=utf-8\n\r\nOK";
            socketChannel.write(ByteBuffer.wrap(ok.getBytes(StandardCharsets.UTF_8)));
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
