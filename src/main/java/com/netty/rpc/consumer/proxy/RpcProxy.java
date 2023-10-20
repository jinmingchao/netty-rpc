package com.netty.rpc.consumer.proxy;

import com.netty.rpc.protocol.InvokeProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {

    //TODO: 研究一下这种泛型的写法
    public static <T> T create(Class<?> clazz) {
        MethodProxy proxy = new MethodProxy(clazz);
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, proxy);
        return result;
    }

    //将本地调用的方式，通过代理的形式变成网络调用
    private static class MethodProxy implements InvocationHandler {

        private Class<?> clazz;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        // TODO: 没懂
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) { //如果是实现类
                return method.invoke(this, args); //直接调用实现类并返回
            } else { //如果是接口
                return rpcInvoker(proxy, method, args);
            }
        }

        private Object rpcInvoker(Object proxy, Method method, Object[] args) {
            //先要构造一个协议的内容, 消息，用于发送
            InvokeProtocol msg = new InvokeProtocol();
            msg.setClassName(this.clazz.getName());
            msg.setMethodName(method.getName());
            msg.setParams(method.getParameterTypes());
            msg.setValues(args);

            //发起网络请求
            EventLoopGroup workgroup = new NioEventLoopGroup();
            final RpcProxyHandler proxyHandler = new RpcProxyHandler();

            try {

                Bootstrap client = new Bootstrap();
                client.group(workgroup);
                client.channel(NioSocketChannel.class);
                client.option(ChannelOption.TCP_NODELAY, true);
                client.handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        /**
                         * pipline的处理逻辑如下:
                         *      1. 处理逻辑的编,解码
                         *      2.
                         *      3.
                         *      4.
                         *      5.
                         */

                        //1. 处理逻辑的编,解码
                        //TODO:了解一下这个构造器的参数要怎么配置
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));//自定义解码器
                        pipeline.addLast(new LengthFieldPrepender(4));//自定义编码器
                        //实参处理
                        pipeline.addLast("encoder", new ObjectEncoder());
                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));//禁缓存

                        // 前面的编解码，就是完成对数据的解析
                        // 最后一步，执行属于自己的逻辑
                        pipeline.addLast(proxyHandler);
                    }
                });

                // 把信息发送过去
                ChannelFuture future = client.connect("localhost", 8080).sync();
                future.channel().writeAndFlush(msg).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workgroup.shutdownGracefully();
            }
            //将localhost:8080服务返回的信息返回
            return proxyHandler.getResponse();
        }
    }
}
