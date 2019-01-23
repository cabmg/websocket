package com.ggzn.websocket;

import com.ggzn.websocket.handler.HttpRequestHandler;
import com.ggzn.websocket.handler.TextWebSocketFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: cabmg
 * Date: 2018/11/13
 * Time: 9:58
 */
@Component
public class SecureChatServerInitializer extends ChannelInitializer<SocketChannel> {

	private final HttpRequestHandler httpRequestHandler;

	private final TextWebSocketFrameHandler textWebSocketFrameHandler;

	@Autowired
	public SecureChatServerInitializer(HttpRequestHandler httpRequestHandler, TextWebSocketFrameHandler textWebSocketFrameHandler) {

		this.httpRequestHandler = httpRequestHandler;
		this.textWebSocketFrameHandler = textWebSocketFrameHandler;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		//设置心跳检测事件
		pipeline.addLast(new IdleStateHandler(0, 0, 120));

		//设置请求和应答编码为
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new HttpObjectAggregator(65536));
		pipeline.addLast(httpRequestHandler);
		pipeline.addLast(textWebSocketFrameHandler);
		pipeline.addLast(new WebSocketServerProtocolHandler("/ggzn/ws"));
	}
}