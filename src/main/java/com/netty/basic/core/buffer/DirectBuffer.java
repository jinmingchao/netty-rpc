package netty.core.buffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 直接缓冲区
 *
 * 关系到Zero copy 与 DMA(Direct Memory Access)的应用
 *
 * Zero Copy(零拷贝)
 * 通常情况下，Java程序读取设备中(比如网卡)的数据
 * 需要先将设备的内存中的数据拷贝到系统内存中
 * 再将系统内存中的数据拷贝到JVM管理的把内存中，供程序使用
 * 直接缓冲区建立在JVM以外的系统内存中
 * 在使用时直接将设备内存中数据拷贝到这块儿系统内存中进行操作
 * 去掉了将系统内存中的信息拷贝到JVM内存中的步骤
 * 所以叫做零拷贝
 *
 * 零拷贝的优点:
 *        提高java程序的IO效率
 *       缺点:
 *       JVM中只保有这块儿直接缓冲区的地址，并没有保存数据，所以管理效率较差
 *
 */
public class DirectBuffer {

    private static final String filepath = "C:\\工作\\项目\\juc\\src\\netty\\core\\buffer\\test2.txt";

    private static final String newFilepath = "C:\\工作\\项目\\juc\\src\\netty\\core\\buffer\\test3.txt";

    public static void main(String[] args) {
        //读文件
        try {
            FileInputStream fis = new FileInputStream(filepath);
            FileChannel inputChannel = fis.getChannel();

            //将内容写到一个新的文件中
            FileOutputStream fos = new FileOutputStream(newFilepath);
            FileChannel outputChannel = fos.getChannel();

            //使用ByteBuffer.allocateDirect()，申请直接存储
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

            while (true) {
                buffer.clear();

                int r = inputChannel.read(buffer); //数据读入缓冲区buffer

                if(r == -1) {
                    break;
                }

                buffer.flip();

                outputChannel.write(buffer);//buffer中内容写出到设备
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
