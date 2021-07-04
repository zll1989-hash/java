package com.itcast.rpc.server.connector.handler;

import com.itcast.common.data.RpcResponse;
import com.itcast.common.utils.JsonSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 服务端编码器
 */
public class RpcServerEncodeHandler extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse znsResponse, ByteBuf byteBuf)
            throws Exception {
        byte[] bytes = JsonSerializerUtil.serialize(znsResponse);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
