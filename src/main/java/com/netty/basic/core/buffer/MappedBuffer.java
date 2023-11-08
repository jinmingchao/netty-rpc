package netty.core.buffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedBuffer {

    private static final String filepath = "C:\\工作\\项目\\juc\\src\\netty\\core\\buffer\\test4.txt";
    static private final int start = 0;
    static private final int size = 26;

    public static void main(String[] args) {
        try {
            RandomAccessFile raf = new RandomAccessFile(filepath,"rw");
            FileChannel channel = raf.getChannel();
            
            //把缓冲区和文件进行一个映射关联
            //只要操作缓冲区里面的内容, 文件内容也会跟着修改
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE,start,size);

            for (int idx = 0, c = 97 - 26 - 6; idx < size; idx++) {
                buffer.put(idx,(byte)c++);
                System.out.println("---"+buffer.position()+"---"); //对于MappedByteBuffer来说, position无效
            }
            //持续写入无需clear(), flip()等操作
//            System.out.println("position-"+ buffer.position());
//            System.out.println("limit-"+ buffer.limit());
//            buffer.clear();
//            System.out.println("position-"+ buffer.position());
//            System.out.println("limit-"+ buffer.limit());

            for (int idx = 1, c = 98; idx < size; idx++) {
                if((idx & 1) == 1) {
                    buffer.put(idx, (byte) c);
                    c+=2;
                }
            }

            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
