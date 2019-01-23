package com.ggzn.websocket.handler;

import com.ggzn.websocket.common.Jwt;
import com.ggzn.websocket.common.WebSocketConstants;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Author: cabmg
 * Date: 2018/11/13
 * Time: 11:20
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {

        String uri = StringUtils.substringBefore(request.uri(), "?");
        //获取webSocket参数
        if ("/ggzn/ws".equals(uri)) {

            //获取地址参数
            QueryStringDecoder query = new QueryStringDecoder(request.uri());
            List<String> token = query.parameters().get("token");

//            Long userId = Jwt.getAppUID(token.get(0));
            WebSocketConstants.userChannel.put(111l,ctx.channel());
            ctx.channel().attr(WebSocketConstants.CHANNEL_TOKEN_KEY).getAndSet(111+"");
            request.setUri(uri);
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {
                send100ContinueExpected(ctx);
            }

            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);

            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }

        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Attribute<String> attr = ctx.channel().attr(WebSocketConstants.CHANNEL_TOKEN_KEY);

        String userId = attr.get();
        if(StringUtils.isNotBlank(userId)){
            WebSocketConstants.userChannel.remove(Long.valueOf(userId));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("错误信息：",cause);
        cause.printStackTrace();
        ctx.close();
    }

    private void send100ContinueExpected(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONFLICT);
        ctx.writeAndFlush(response);
    }
}