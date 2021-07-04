package com.itcast.rpc.server.connector;

import com.itcast.rpc.server.connector.handler.RpcRequestHandler;
import com.itcast.rpc.server.connector.handler.RpcServerDecodeHandler;
import com.itcast.rpc.server.connector.handler.RpcServerEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端Netty连接初始化配置
 */
@Component
@ChannelHandler.Sharable
public class RpcServerInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private RpcRequestHandler znsRequestHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new RpcServerDecodeHandler())
                .addLast(new RpcServerEncodeHandler())
                .addLast(znsRequestHandler);
    }
}
