package netty.io.nio.server;

public class App {

    public static void main(String[] args) {
        new NIOServer(8080).listen();
    }
}
