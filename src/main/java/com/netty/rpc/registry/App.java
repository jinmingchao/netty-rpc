package com.netty.rpc.registry;

public class App {
    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }
}
