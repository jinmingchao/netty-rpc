package com.netty.rpc.consumer.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {

    //TODO: 研究一下这种泛型的写法
    public static <T> T create(Class<?> clazz){
        MethodProxy proxy = new MethodProxy();
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, proxy);
        return result;
    }

    //将本地调用的方式，通过代理的形式变成网络调用
    private static class MethodProxy implements InvocationHandler{

        private Class<?> clazz;

        public MethodProxy (Class<?> clazz) {
                this.clazz = clazz;
        }

        // TODO: 没懂
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) { //如果是实现类
                    return method.invoke(this,args); //直接调用实现类并返回
            } else { //如果是接口

            }
            return null;
        }
    }
}
