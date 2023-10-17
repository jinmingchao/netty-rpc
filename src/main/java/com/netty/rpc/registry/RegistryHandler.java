package com.netty.rpc.registry;

import com.netty.rpc.protocol.InvokeProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理逻辑
 * 1. 根据一个包名，将所有符合条件的class全部扫描出来，放到一个容器中 (这里是简化版本)
 * 如果是分布式, 就是读配置文件
 * 2. 给每一个对应的class起一个唯一名字，作为服务名称，保存到一个容器中
 * 3. 当有客户端连接过来之后，就会获取协议内容，即InvokerProtocol的对象
 * 4. 要去注册好的容器中去找到符合条件的服务
 * 5. 通过远程调用Provider得到返回结果，并回复给客户端
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    private List<String> classNames = new LinkedList<>();

    private Map<String, Object> registryMap = new ConcurrentHashMap<>();

    public RegistryHandler() {
        // 1. 根据一个包名，将所有符合条件的class全部扫描出来，放到一个容器中 (这里是简化版本)
        //    如果是分布式, 就是读配置文件
        scannerClass("com.netty.rpc.provider");
        //  2. 给每一个对应的class起一个唯一名字，作为服务名称，保存到一个容器中
        doRegistry();
    }

    //正常情况下，应该是读配置文件获取包名，即把一个包名写在配置文件内
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }

    }

    private void doRegistry() {
        if (classNames.isEmpty()) {
            return;
        }

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0]; //获取clazz实现的第一个接口对象
                String serviceName = i.getName();
                // 本来这里存的应该是网络路径, 从配置文件读取, 在调用的时候再去解析
                // 这里简化，直接用反射调用获取对象

                registryMap.put(serviceName, clazz.getDeclaredConstructor().newInstance());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }


    }


    /**
     * 有客户端连接上时，回调该方法
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    //3. 当有客户端连接过来之后，就会获取协议内容，即InvokerProtocol的对象
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Object result = new Object();
        InvokeProtocol request = (InvokeProtocol) msg;

        // 4. 要去注册好的容器中去找到符合条件的服务
        if (registryMap.containsKey(request.getClassName())) {
            Object service = registryMap.get(request.getClassName());
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParams());//形参找方法
            method.invoke(service, request.getValues());//实参参与调用
        }
        // 5. 通过远程调用Provider得到返回结果，并回复给客户端
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    /**
     * 连接发生异常时，回调该方法
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
