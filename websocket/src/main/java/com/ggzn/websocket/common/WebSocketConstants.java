package com.ggzn.websocket.common;


import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebSocketConstants {

    public static final AttributeKey<String> CHANNEL_TOKEN_KEY = AttributeKey.valueOf("netty.channel.token");

    /**
     * 用来缓存用户channel信息
     */
    public static final ConcurrentMap<Long, Channel> userChannel = new ConcurrentHashMap<>();

    public static final ConcurrentMap<Integer, ChannelGroup> onlineRooms = new ConcurrentHashMap<>();

}
