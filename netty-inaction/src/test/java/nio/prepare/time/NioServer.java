package nio.prepare.time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private long timeOut=2000;

    public NioServer(){
        try {
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(9090));
            selector=Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
           System.exit(1);
        }
    }


    public void start(){
        try {
            while (true){
                selector.select(timeOut);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()){
                    SelectionKey selectionKey = keyIterator.next();
                    if(selectionKey.isValid()){
                        if(selectionKey.isAcceptable()){
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector,SelectionKey.OP_READ);
                        }
                        if(selectionKey.isReadable()){
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer=ByteBuffer.allocate(1024);
                            socketChannel.read(buffer);
                            System.out.println(new String(buffer.array()));
                            ByteBuffer buffer1=ByteBuffer.wrap(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()).getBytes());
                            socketChannel.write(buffer1);
                        }
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NioServer().start();
    }




}
