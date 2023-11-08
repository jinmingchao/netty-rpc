package netty.tomcat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import netty.tomcat.netty.http.GPServlet;
import netty.tomcat.netty.util.GPTomcatHandler;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Netty就是一个同时支持多协议的网络通信版本
 */
public class GPTomcat {
    //J2EE标准
    //Servlet
    //Request
    //Response

    // 1. 配置好启动端口, 默认是8080 ServerSocket IP:localhost
    // 2. 配置一个web.xml文件 自己写的servlet 要集成HttpServlet
    //    servlet-name
    //    servlet-class
    //    url-pattern
    // 3. 读取配置, url-pattern 和Servlet建立一个映射关系
    // Map servletMapping
    // 4. 用户发HTTP请求, 发送的数据就是字符串, 有规律的字符串(符合HTTP协议的)
    // 5. 从协议内容中拿到URL，把相应的Servlet用反射进行实例化
    // 6. 调用实例化对象的service()方法, 执行具体的逻辑, 即doGet/doPost方法
    // 7. 加入request(inputStream)和response(outputStream)对象

    private int port = 8080;

    private ServerSocket server;

    public static Map<String, GPServlet> servletMapping = new HashMap<>();

    private Properties webxml = new Properties();

    public GPTomcat() {
        init();
    }

    private void init() {

        // 加载web.xml文件, 同时初始化ServletMapping
        String WEB_INF = this.getClass().getResource("/").getPath();
        try {
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
            webxml.load(fis);

            for (Object k : webxml.keySet()) {
                //遍历看看
                //System.out.println(key.toString()+ " : " + webxml.get(key).toString());
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    //System.out.println(servletName);
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    //servlet是单例的, 如果调用多个请求调用一个单例，会用多线程执行service()方法
                    if(className.indexOf("netty.tomcat.netty.servlet") > -1) {
                        GPServlet servletObject = (GPServlet) Class.forName(className).getDeclaredConstructor().newInstance();
                        servletMapping.put(url, servletObject);
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        //1. 通过配置文件，初始化servletMapping

        //2.Netty版
        //Netty封装了NIO, Reactor模型，boss/work线程
        //Boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //Work线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // Netty 服务
            ServerBootstrap server = new ServerBootstrap(); //ServerSocketChannel的封装

            // 链路式编程
            server.group(bossGroup, workerGroup)
                    //主线程处理类, 看到这样的写法，底层就是用反射
                    .channel(NioServerSocketChannel.class)
                    // 子线程处理类, Handler
                    .childHandler(new ChannelInitializer<>() {
                        // 客户端初始化处理
                        @Override
                        protected void initChannel(Channel client) throws Exception {
                            // 无锁化串行编程
                            // Netty对HTTP协议的封装, 顺序有要求
                            // HttpResponseEncoder 编码器
                            client.pipeline().addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder 解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //业务逻辑处理
                            client.pipeline().addLast(new GPTomcatHandler());

                        }
                    })
                    // 针对主线程的配, 分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 针对子线程的配置, 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 启动服务器
            ChannelFuture f = server.bind(port).sync();
            System.out.println("GP Tomcat已启动, 监听的端口是: " + port  );
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
