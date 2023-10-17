package com.netty.rpc.consumer;

import com.netty.rpc.api.IRpcHelloService;
import com.netty.rpc.api.IRpcService;
import com.netty.rpc.consumer.proxy.RpcProxy;
import com.netty.rpc.provider.RpcHelloServiceImpl;

public class RpcConsumer {
    public static void main(String[] args) {
        //这是本地调用
        IRpcHelloService helloService = new RpcHelloServiceImpl();
        System.out.println(helloService.hello("Roland"));
        //这是代理实现
        IRpcHelloService helloService_1 = RpcProxy.create(IRpcHelloService.class);
        System.out.println(helloService_1.hello("Roland"));

        IRpcService service = RpcProxy.create(IRpcService.class);
        System.out.println("8 + 2 = " + service.add(8,2));
        System.out.println("8 - 2 = " + service.sub(8,2));
        System.out.println("8 * 2 = " + service.multi(8,2));
        System.out.println("8 / 2 = " + service.divide(8,2));
    }
}
