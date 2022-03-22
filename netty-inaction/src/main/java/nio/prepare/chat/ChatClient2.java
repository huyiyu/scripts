package nio.prepare.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ChatClient2 {

    private SocketChannel socketChannel;

    private String userName;

    public ChatClient2(){
        try {
            //得到一个网络通道
            socketChannel=SocketChannel.open();
            //设置非阻塞式
            socketChannel.configureBlocking(false);
            //提供服务器ip与端口
            InetSocketAddress inetSocketAddress=new InetSocketAddress("127.0.0.1",9090);
            //连接服务器端
            if(!socketChannel.connect(inetSocketAddress)){     //如果连接不上
                while(!socketChannel.finishConnect()){
                    System.out.println("nio非阻塞");
                }
            }
            userName=inetSocketAddress.getHostString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void writeData(String str) throws IOException {
        if(str.equals("by")){
            socketChannel.close();
            return;
        }
        ByteBuffer byteBuffer=ByteBuffer.wrap((userName+"说："+str).getBytes());
        socketChannel.write(byteBuffer);
    }

    public void readData() throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        int read = socketChannel.read(byteBuffer);
        if(read>0){
            byte[] array = byteBuffer.array();
            System.out.println(new String(array));
        }
    }


    public static void main(String[] args) throws IOException {
        ChatClient2 chatClient=new ChatClient2();

        //读数据
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        chatClient.readData();
                        sleep(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //写数据
        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            chatClient.writeData(s);
        }
    }


}
