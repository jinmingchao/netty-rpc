package netty.tomcat.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.io.InputStream;
import io.netty.handler.codec.http.HttpRequest;

public class GPRequest {

    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public GPRequest(ChannelHandlerContext ctx, HttpRequest req) {
                this.ctx = ctx;
                this.req = req;
    }

    public String getUrl() {
        return req.uri();
    }

    public String getMethod() {
        return req.method().toString();
    }
}
