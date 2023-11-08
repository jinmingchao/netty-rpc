package netty.io.bio;

public class App {
    public static void main(String[] args) {
        byte[] bs = new byte[10];
        char v = 'a';
        for(int i = 0; i < 10; ++i) {
            bs[i] = (byte) v++;
        }

        System.out.println(new String(bs,0,5));
        System.out.println(new String(bs,5,5));
    }
}
