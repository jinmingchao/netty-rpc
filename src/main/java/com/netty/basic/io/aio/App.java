package netty.io.aio;

public class App {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new AIOServer(8080).listen();
            }
        }).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                new AIOClient().connect("localhost", 8080);
            }
        }).start();

    }
}
