package netty.tomcat.netty.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import netty.tomcat.netty.GPTomcat;
import netty.tomcat.netty.http.GPRequest;
import netty.tomcat.netty.http.GPResponse;


public class GPTomcatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            //转交给我们自己的request 实现
            GPRequest request = new GPRequest(ctx, req);
            //转交给我们自己的response 实现
            GPResponse response = new GPResponse(ctx , req);
            //实际业务处理
            String url = request.getUrl();

            if (GPTomcat.servletMapping.containsKey(url)) {
                GPTomcat.servletMapping.get(url).service(request, response);
            } else {
                response.write("404 - Not Found");
            }
        }
    }
}
