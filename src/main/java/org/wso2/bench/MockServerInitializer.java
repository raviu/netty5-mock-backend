package org.wso2.bench;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class MockServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private int delay;
    private String responseContentType;
    private MockServerThread[] handlers;

    public MockServerInitializer(SslContext sslCtx, int delay, String responseContentType, MockServerThread[] handlers) {
        this.sslCtx = sslCtx;
        this.delay = delay;
        this.responseContentType = responseContentType;
        this.handlers = handlers;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));

        //HttpResponseDelay Handler
//        p.addLast(new HttpResponseDelayHandler());

        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());

        MockServerHandler snoopServerHandler = new MockServerHandler();
        snoopServerHandler.setDelay(this.delay);
        snoopServerHandler.setResponseContentType(this.responseContentType);
        snoopServerHandler.setHandlers(handlers);
        p.addLast(snoopServerHandler);
    }
}
