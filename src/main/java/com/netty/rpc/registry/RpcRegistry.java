package com.netty.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.nio.channels.Channel;
import java.rmi.registry.Registry;

public class RpcRegistry {

    private Integer port;

    public RpcRegistry(int port ) {
        this.port = port;
    }

    public void start() {
        try {


        // ServerBootstrap 对应 ServerSocket / ServerSocketChannel
        ServerBootstrap server = new ServerBootstrap();

        //bossGroup 对应 nio中的 Selector, 一个主线程的线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        //workerGroup 对应工作线程池，对应NIO中处理具体业务的线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        server.group(bossGroup, workerGroup);
        server.channel(NioServerSocketChannel.class); //指定selector上注册的channel类?
        server.childHandler(new ChannelInitializer<SocketChannel>() { //子线程具体业务逻辑

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                // 在Netty中, 把所有的业务逻辑处理都归总要一个队列中
                // 这个队列中包含了各种各样的业务逻辑, 这些处理逻辑在Netty中有一个封装
                // 封装成了一个对象, 无锁化串行任务队列
                // 即pipline, 就是对我们处理逻辑的封装

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
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4 ));//自定义解码器
                pipeline.addLast(new LengthFieldPrepender(4));//自定义编码器
                //实参处理
                pipeline.addLast("encoder",new ObjectEncoder());
                pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));//禁缓存

                // 前面的编解码，就是完成对数据的解析
                // 最后一步，执行属于自己的逻辑
                // 1. 注册, 给每一个对象起一个名字，这个名字就是对外提供服务的名字
                // 2. 服务位置要做一个登记

                pipeline.addLast(new RegistryHandler());
            }
        });

        server.option(ChannelOption.SO_BACKLOG,128);// Selector上可注册的channel key的最大值为128
        server.childOption(ChannelOption.SO_KEEPALIVE, true); // 工作线程池中的线程是可回收再用的

        //正式启动服务，相当于用一个死循环开始轮训
        ChannelFuture future = server.bind(this.port).sync(); //开始监听，阻塞, 等待工作线程返回future
        System.out.println("GP RPC Registry start listening at " + this.port);
        future.channel().closeFuture().sync(); //?
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
