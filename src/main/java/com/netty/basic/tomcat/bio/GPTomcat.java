package netty.tomcat.bio;

import netty.tomcat.bio.http.GPRequest;
import netty.tomcat.bio.http.GPResponse;
import netty.tomcat.bio.http.GPServlet;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
                    GPServlet servletObject = (GPServlet) Class.forName(className).getDeclaredConstructor().newInstance();
                    servletMapping.put(url, servletObject); // key: web.properties中servlet.two.url对应的值
                    // value: web.properties中servlet.two.className的值的类的实例
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

        //2.原始版
        try {
            server = new ServerSocket(this.port);
            System.out.println("GPTomcat已启动, 监听的端口是: " + this.port);
            //2. 等待用户请求, 用一个死循环来等待用户请求
            while (true) {
                Socket client = server.accept();
                process(client);//处理用户请求
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void process(Socket client) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = client.getInputStream();
            os = client.getOutputStream();

            GPRequest request = new GPRequest(is);
            GPResponse response = new GPResponse(os);

            String url = request.getUrl();
            //从请求中获取url信息，从servletMapping中取出对应的servlet单例
            if (servletMapping.containsKey(url)) {
                GPServlet servletObj = servletMapping.get(url);
                //调用相应单例的service方法
                servletObj.service(request, response);
            } else {
                response.write("404 - Not Found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }
}
