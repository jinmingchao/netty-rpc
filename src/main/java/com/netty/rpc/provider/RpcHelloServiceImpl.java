package com.netty.rpc.provider;

import com.netty.rpc.api.IRpcHelloService;

public class RpcHelloServiceImpl implements IRpcHelloService {

    @Override
    public String hello(String name) {
        return "Hello " + name + "!";
    }
}
