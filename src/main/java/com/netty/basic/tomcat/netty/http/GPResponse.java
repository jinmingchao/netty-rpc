package netty.tomcat.netty.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import io.netty.handler.codec.http.HttpRequest;
import java.nio.charset.StandardCharsets;

public class GPResponse {

    /**
     * SocketChannel的封装
     */
    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public GPResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String msg) {
       try {
           if (msg == null || msg.length() == 0) {
               return;
           }

           //设置Http协议以及请求头信息
           FullHttpResponse response = new DefaultFullHttpResponse(
                   //设置http版本为1.1
                   HttpVersion.HTTP_1_1,
                   //设置响应状态码
                   HttpResponseStatus.OK,
                   //将输出值写出 编码为UTF-8
                   Unpooled.wrappedBuffer(msg.getBytes(StandardCharsets.UTF_8))
           );

           response.headers().set("Content-Type","text/html;");
           ctx.write(response);
       } catch (Exception e){
            e.printStackTrace();
       } finally {
           ctx.flush();
           ctx.close();
       }
    }
}
