package nio.prepare.reactor.muitiple;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
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
        String id = Thread.currentThread().getName();
        System.out.println("thread:" + id + " socket:" + socketChannel.hashCode());
        System.out.println("thread:" + id + " worker:" + this.hashCode());
        Thread thread = new Thread(this::process);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.start();
    }

    private synchronized void process() {
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
