package com.itcast.rpc.server.connector;

import com.itcast.common.utils.SpringBeanFactory;
import com.itcast.rpc.server.config.RpcServerConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rpc服务端连接接收器
 */
public class RpcServerAcceptor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerAcceptor.class);

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();

    private RpcServerConfiguration znsServerConfiguration;
    private RpcServerInitializer znsServerInitializer;

    public RpcServerAcceptor() {
        this.znsServerConfiguration = SpringBeanFactory.getBean(RpcServerConfiguration.class);
        this.znsServerInitializer = SpringBeanFactory.getBean(RpcServerInitializer.class);
    }

    @Override
    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(znsServerInitializer);

        try {
            LOGGER.info("ZnsServer acceptor startup at port[{}] successfully", znsServerConfiguration.getNetworkPort());

            ChannelFuture future = bootstrap.bind(znsServerConfiguration.getNetworkPort()).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("ZnsServer acceptor startup failure!", e);
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully().syncUninterruptibly();
            worker.shutdownGracefully().syncUninterruptibly();
        }
    }
}
