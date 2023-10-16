package com.netty.rpc.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 处理逻辑
 * 1. 根据一个包名，将所有符合条件的class全部扫描出来，放到一个容器中
 *    如果是分布式, 就是读配置文件
 * 2. 给每一个对应的class起一个唯一名字，作为服务名称，保存到一个容器中
 * 3. 当有客户端连接过来之后，就会获取协议内容，即InvokerProtocol的对象
 * 4. 要去注册好的容器中去找到符合条件的服务
 * 5. 通过远程调用Provider得到返回结果，并回复给客户端
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    /**
     * 有客户端连接上时，回调该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    /**
     * 连接发生异常时，回调该方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
