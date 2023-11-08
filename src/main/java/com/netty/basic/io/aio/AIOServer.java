package netty.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIOServer {

    //服务用端口号
    private Integer port = 8080;

    public AIOServer(Integer port) {
        this.port = port;
    }

    void listen() {
        ExecutorService pool = Executors.newCachedThreadPool();
        AsynchronousChannelGroup threadGroup = null;
        try {
            threadGroup = AsynchronousChannelGroup.withCachedThreadPool(pool, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //服务端准备就绪
            //需要添加参数 threadGroup: 工作线程，用来监听回调的, 事件响应的时候需要回调
            final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(threadGroup);
            server.bind(new InetSocketAddress(port));//绑定端口号
            System.out.println("服务已启动，监听端口：" + port);

            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                final ByteBuffer buffer = ByteBuffer.allocateDirect(32);
                //completed() 接收成功的回调函数，Channel收到信息后的处理逻辑在这里
                //failed() 是失败的回调函数

                /**
                 * Invoked when an operation has completed.
                 *
                 * @param   result
                 *          The result of the I/O operation.
                 * @param   attachment
                 *          The object attached to the I/O operation when it was initiated.
                 */
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    System.out.println("IO操作成功, 开始获取数据");
                    try {
                        buffer.clear();
                        result.read(buffer).get();
                        buffer.flip();
                        result.write(buffer);
                        buffer.flip();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println(e.toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            result.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.accept(null, this);
                    }

                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("IO操作失败: "  + exc.getMessage());
                }
            });

        Thread.sleep(Integer.MAX_VALUE);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
