package nio.prepare.time;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    private SocketChannel socketChannel;
    private Selector selector;
    private long timeOut=2000;

    public NioClient(){
        try {
            socketChannel=SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector=Selector.open();
            if(socketChannel.connect(new InetSocketAddress("127.0.0.1",9090))){
                socketChannel.register(selector, SelectionKey.OP_READ);
            }else{
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                        if(selectionKey.isConnectable()){
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            if(socketChannel.finishConnect()){
                                socketChannel.register(selector,SelectionKey.OP_READ);
                                ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                                byteBuffer.put("你好".getBytes());
                                byteBuffer.flip();
                                socketChannel.write(byteBuffer);
                            }else{
                                System.err.println("可以尝试重试");
                            }
                        }
                        if(selectionKey.isReadable()){
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer=ByteBuffer.allocate(1024);
                            socketChannel.read(buffer);
                            System.out.println(new String(buffer.array()));
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
        new NioClient().start();
    }
}
