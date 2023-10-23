package com.netty.rpc.registry;

public class App {
    public static void main(String[] args) {
//        try {
//            Class clazz = Class.forName("com.netty.rpc.provider.RpcHelloServiceImpl");
//            for (Class anInterface : clazz.getInterfaces()) {
//                System.out.println(anInterface.getName());
//            }
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        new RpcRegistry(8080).start();
    }
}
