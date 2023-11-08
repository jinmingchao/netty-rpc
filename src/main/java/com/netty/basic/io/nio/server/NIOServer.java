package netty.io.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    //轮询器 Selector - 大堂经理
    private Selector selector;

    //缓冲区 Buffer - 等候区
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    //服务用端口号
    private Integer port = 8080;

    public NIOServer(Integer port) {

       this.port = port;
       //初始化大堂经理，开门营业
        ServerSocketChannel channel = null;
        try {
            channel = ServerSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //告知请求方地址，方便接客
        try {
            channel.bind(new InetSocketAddress("127.0.0.1",this.port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //由于NIO是BIO的升级版，所以为了兼容BIO，NIO默认是采用BIO的阻塞模式
        try {
            channel.configureBlocking(false); //设置成非阻塞模式
        } catch (IOException e) {
            e.printStackTrace();
        }
        //大堂经理准备就绪
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //注册关联 selector, channel. 并声明操作 (on accept);
        try {
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }

    }

    public void listen() {
        System.out.println("Listen on port " + this.port+".");
        int cnt = 0;
        while (true) {
            int num_0 = 0, num_1 = 0;
            try {
               num_0 = selector.select(); //blocking here.
               // System.out.println();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> keys = selector.selectedKeys(); //这个set不是thread safe的
            num_1 = keys.size();
            Iterator<SelectionKey> keyIterator = keys.iterator();
            //同步体现在这里, 因为每次只能拿一个key, 每次只能处理一种状态
            while (keyIterator.hasNext()) { //TODO 改成多线程 process 版本
                SelectionKey next = keyIterator.next();
                keyIterator.remove();//删除最后一个元素
                //SelectionKey 是一种状态，表示对应的Channel中数据的准备状态
                boolean b = process(next);
            }

            System.out.printf("loop: %s | selector.select(): %s | keys.size(): %s \n",++cnt, num_0, num_1);
            //System.out.println("loop: " + (++cnt) +",");
        }
    }

    private boolean process(SelectionKey key) {
        StringBuilder status = new StringBuilder();
        if (key.isWritable()) {
            status.append("WRITABLE ");
        } else if(key.isReadable()) {
            status.append("READABLE ");
        } else if(key.isAcceptable()) {
            status.append("ACCEPTABLE ");
        } else if(key.isValid()) {
            status.append("VALID ");
        } else if(key.isConnectable()) {
            status.append("CONNECTABLE ");
        }
        System.out.println("key's status: " + status.toString());
         //处理SelectionKey的不同状态
       if (key.isAcceptable()) { //通道准备就绪
           ServerSocketChannel server = (ServerSocketChannel) key.channel();
           SocketChannel channel = null;
           try {
               //System.out.println("acceptable channel is blocking start.");
               channel = server.accept();
               //System.out.println("acceptable channel is blocking end.");
           } catch (IOException e) {
               e.printStackTrace();
           }
           //System.out.println("acceptable channel's: " + channel );
           try {
               channel.configureBlocking(false);
           } catch (IOException e) {
               e.printStackTrace();
           }

           try {
               //当数据准备好的时候, 将状态改成可读。
               channel.register(selector, SelectionKey.OP_READ);
           } catch (ClosedChannelException e) {
               e.printStackTrace();
           }
       }
       else if(key.isReadable()) { //数据可被读取

           // (SocketChannel) key.channel() 从多路复用器中拿到客户端的引用
           SocketChannel channel = (SocketChannel) key.channel();
           int len = 0;
           try {
               len = channel.read(buffer);
           } catch (IOException e) {
               e.printStackTrace();
           }

           if( len > 0) {
               buffer.flip();// ???
               String content = new String (buffer.array(), 0, len);
               System.out.println("读取到的数据: " + content);

               content += "-have read";

               try {
                  key = channel.register(selector, SelectionKey.OP_WRITE); // ???
                   // key上携带读到的内容一会再写出去;
                   key.attach(content);
               } catch (ClosedChannelException e) {
                   e.printStackTrace();
               }

           }
       }
       else if(key.isWritable()) { // channel 可以写入数据
           SocketChannel channel = (SocketChannel) key.channel();
           String content = (String)key.attachment();
           try {
               // 将key携带的内容写出
               channel.write(ByteBuffer.wrap(("输出: " + content).getBytes()));
               channel.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       return true;
    }


}
