package org.wso2.bench;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


public class DelayedElement implements Delayed {

    private long DELAY;
    private ChannelHandlerContext ctx;
    private LastHttpContent trailer;
    private HttpRequest request;
    private byte[] buf;
    private String responseContentType;
    protected long timestamp;

    public DelayedElement(ChannelHandlerContext ctx, LastHttpContent trailer, HttpRequest request, byte[] buf, long receivedTime, int delay, String responseContentType) {
        DELAY = delay;
        this.ctx = ctx;
        this.trailer = trailer;
        this.request = request;
        this.buf = buf;
        this.timestamp = receivedTime;
        this.responseContentType = responseContentType;
    }

    public long getDelay(TimeUnit unit) {
        return DELAY - (System.currentTimeMillis() - this.timestamp);
    }

    public int compareTo(Delayed other) {
        long comparison = ((org.wso2.bench.DelayedElement) other).timestamp - this.timestamp;

        if(comparison > 0) {
            return -1;
        } else if(comparison < 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public byte[] getBuf() {
        return buf;
    }

    public ChannelHandlerContext getContext() {
        return ctx;
    }

    public LastHttpContent getTrailer() {
        return trailer;
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    public String getResponseContentType() {
        return this.responseContentType;
    }

}
