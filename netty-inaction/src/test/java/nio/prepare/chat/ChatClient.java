package nio.prepare.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ChatClient implements  Runnable{

    private SocketChannel socketChannel;

    private Selector selector;

    public ChatClient(){
        try {
            //得到一个网络通道
            socketChannel=SocketChannel.open();
            selector=Selector.open();
            //设置非阻塞式
            socketChannel.configureBlocking(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void doCon(){
        //提供服务器ip与端口
        InetSocketAddress inetSocketAddress=new InetSocketAddress("127.0.0.1",9090);
        //连接服务器端
        try {
            if(socketChannel.connect(inetSocketAddress)){
                socketChannel.register(selector,SelectionKey.OP_READ);
                //写数据
                writeData(socketChannel);
            }else{
                socketChannel.register(selector, SelectionKey.OP_CONNECT);//如果连接不上
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeData(SocketChannel socketChannel) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        Scanner scanner=new Scanner(System.in);
                        String str = scanner.nextLine();
                        if(str.equals("by")){
                            socketChannel.close();
                            return;
                        }
                        ByteBuffer byteBuffer=ByteBuffer.wrap((socketChannel.getLocalAddress().toString()+"说："+str).getBytes());
                        socketChannel.write(byteBuffer);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void readData() throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);
        if(read>0){
            byte[] array = byteBuffer.array();
            System.out.println(new String(array,"utf-8"));
        }
    }


    public static void main(String[] args) throws IOException {
        new Thread(new ChatClient()).start();
    }

    @Override
    public void run() {
        doCon();
        try {
            while (true){
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isValid()){
                        if(selectionKey.isConnectable()){
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            if (channel.finishConnect()){
                                channel.register(selector,SelectionKey.OP_READ);
                                System.out.println("bbbbbbbbbbbbb");
                                //写数据
                                writeData(channel);
                            }else{
                                System.exit(1);
                            }
                        }
                        if(selectionKey.isReadable()){
                            readData();
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
