package netty.core.buffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class App1 {

    private static final String filepath = "C:\\工作\\项目\\juc\\src\\netty\\core\\buffer\\test1.txt";

    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(64);
        // buffer.asReadOnlyBuffer(); //可以指定为只读缓冲区

        try {
            FileInputStream fis = new FileInputStream(filepath);
            FileChannel channel = fis.getChannel();

            output("初始化", buffer);

            channel.read(buffer);//读数据到buffer中
            output("调用read()", buffer);

            buffer.flip();//先锁定操作范围
            output("调用flip()", buffer);
            //判断有无可读数据
            while (buffer.remaining() > 0) {
                byte b = buffer.get();
                System.out.println((char)b);
            }
            output("调用get()", buffer);

            buffer.clear();
            buffer.put("jmc get the msg.".getBytes());
            buffer.flip();
            output("调用put()", buffer);

//           try {
//               channel.write(buffer); //同一个通道不能同时进行读写操作.
//           } catch (NonWritableChannelException e) {
//
//           }
//           output("调用channel.write()", buffer);

            FileOutputStream fos = new FileOutputStream(filepath);
            FileChannel writeChannel = fos.getChannel();
            writeChannel.write(buffer);
            output("调用channel.write()", buffer);

            buffer.clear();
            output("调用clear()", buffer);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void output(String step, ByteBuffer buffer) {
        System.out.println(step + " : ");
        //
        System.out.print("capacity: " + buffer.capacity() + ", ");
        System.out.print("position: "+ buffer.position() + ", ");
        System.out.println("limit: " + buffer.limit());
        System.out.println();
    }
}
