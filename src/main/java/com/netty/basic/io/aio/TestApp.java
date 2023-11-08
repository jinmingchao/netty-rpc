package netty.io.aio;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class TestApp {
    public static void main(String[] args) {
        // 1. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(32);
        //System.out.println(buffer.get(0)); // 0
        //System.out.println(buffer.position());// 0

    }
}
