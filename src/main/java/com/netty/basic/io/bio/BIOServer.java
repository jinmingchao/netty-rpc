package netty.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer {

    public static void main(String[] args) throws IOException {
        ServerSocket service = new ServerSocket(8080);
        System.out.println("Service is waiting.");
        Socket info = service.accept();
        InputStream ips = info.getInputStream();

        int b;
        StringBuilder msg = new StringBuilder();
        while ((b = ips.read()) != -1) {
            msg.append((char)b);
        }

        System.out.println("The message is: " + msg);
    }
}
