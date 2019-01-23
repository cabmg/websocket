package com.ggzn.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * Author: cabmg
 * Date: 2018/11/13
 * Time: 9:58
 */
@Component
@Slf4j
public class WebSocketServer {

    private EventLoopGroup boss;
    private EventLoopGroup work;

    @Autowired
    private SecureChatServerInitializer secureChatServerInitializer;

    private void start() {

        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(secureChatServerInitializer)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        log.info("netty启动....................");
        bootstrap.bind(9000).syncUninterruptibly().channel().closeFuture().syncUninterruptibly();
    }

    @PreDestroy
    public void destroy() {
        boss.shutdownGracefully();
        work.shutdownGracefully();
        log.warn("连接断开");
    }

    @PostConstruct
    public void init() {
        new Thread(this::start).start();
    }
}
