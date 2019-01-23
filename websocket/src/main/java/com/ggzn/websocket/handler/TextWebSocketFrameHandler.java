package com.ggzn.websocket.handler;

import com.ggzn.websocket.common.WebSocketConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Author: cabmg
 * Date: 2018/11/13
 * Time: 9:58
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
                    CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {

            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(
                    ChannelFutureListener.CLOSE_ON_FAILURE);

        }else {
            super.userEventTriggered(ctx, evt);
        }
    }



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        Channel channel = channelHandlerContext.channel();
        String s = channel.attr(WebSocketConstants.CHANNEL_TOKEN_KEY).get();
        System.out.println(text);
        channel.writeAndFlush(new TextWebSocketFrame(text));

    }

    //每个channel都有一个唯一的id值
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //打印出channel唯一值，asLongText方法是channel的id的全名
//        String userId = ctx.channel().attr(WebSocketConstants.CHANNEL_TOKEN_KEY).get();
//        WebSocketConstants.userChannel.put(Long.valueOf(userId),ctx.channel());
//    }

//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//
//        Attribute<String> attr = ctx.channel().attr(WebSocketConstants.CHANNEL_TOKEN_KEY);
//
//        String userId = attr.get();
//        if(StringUtils.isNotBlank(userId)){
//            WebSocketConstants.userChannel.remove(Long.valueOf(userId));
//        }
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("错误信息:",cause);
        cause.printStackTrace();
        handlerRemoved(ctx);
        ctx.close();
    }
}
