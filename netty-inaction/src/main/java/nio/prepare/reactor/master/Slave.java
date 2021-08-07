package nio.prepare.reactor.master;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

public class Slave extends Thread {

    private volatile Selector selector;
    private int bugCount;
    private static final int MAX_ERROR_COUNT = 10;
    private volatile boolean restart = false;

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public Slave(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            while (!Thread.interrupted() && !restart) {
                if (safeSelect(0) == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    Worker attachment = (Worker) selectionKey.attachment();
                    attachment.process();
                }
                selectionKeys.clear();
            }
        }


    }

    /**
     * 重建selector jdk bug 解决
     *
     * @param timeout 超时时间
     * @return select 个数
     * @throws IOException
     */
    public int safeSelect(long timeout) {

        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
