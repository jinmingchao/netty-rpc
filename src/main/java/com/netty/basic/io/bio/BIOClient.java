package netty.io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class BIOClient {

    public static void main(String[] args) throws IOException {
        //BIO向网卡8080端口写数据
        Socket client = new Socket("localhost", 8080);
        
        OutputStream ops = client.getOutputStream();
        String name = UUID.randomUUID().toString();

        System.out.println("客户端发送数据: " + name);
        ops.write(name.getBytes());
        ops.close();
        client.close();
    }
}
