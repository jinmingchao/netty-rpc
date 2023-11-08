package netty.io.nio.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NIOClient {
    //TODO 多线程写一下，和server联调，测一下AIO的API

    public static void main(String[] args) throws IOException {
        //BIO向网卡8080端口写数据
        Socket client = new Socket("localhost", 8080);
        OutputStream ops = client.getOutputStream();
        String name = UUID.randomUUID().toString();

        System.out.println("客户端发送数据: " + name);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ops.write(name.getBytes());
        ops.close();
        client.close();
    }
}
