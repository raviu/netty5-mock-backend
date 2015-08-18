package org.wso2.bench;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class MockServerHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;

    private int DELAY = 0;

    private String responseContentType = "text/plain; charset=UTF-8";

    private MockServerThread[] handlers;

    private static ExecutorService executorService = Executors.newFixedThreadPool(160);


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void messageReceived(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;

            if (HttpHeaderUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            if (msg instanceof LastHttpContent) {

                final LastHttpContent trailer = (LastHttpContent) msg;

                try {

                    executorService.execute(new Runnable() {

                        public ChannelHandlerContext getCtx() {
                            return ctx;
                        }

                        @Override
                        public void run() {
                            handlers[0].delayEvent(ctx, trailer, request, null, DELAY, responseContentType, 0);
                        }
                    });

                } catch (Throwable throwable) {
                    System.out.println("CAUGHT ################ " + throwable);
                }

            }
        }

    }


    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void setDelay(int delay) {
        this.DELAY = delay;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public MockServerThread[] getHandlers() {
        return handlers;
    }

    public void setHandlers(MockServerThread[] handlers) {
        this.handlers = handlers;
    }
}
