package com.netty.httpserver;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.CharsetUtil;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;

	public HttpServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}
	
	@Override
    public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		p.addLast("encoder", new HttpResponseEncoder());
		p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("aggregator", new HttpObjectAggregator(Short.MAX_VALUE));
        p.addLast("handler", new HttpInputHandler());
        p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
	    p.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
	    
    }
    
}