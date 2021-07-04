package com.itcast.rpc.server.connector.handler;

import com.itcast.common.data.RpcRequest;
import com.itcast.common.utils.JsonSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 服务端解码器
 */
public class RpcServerDecodeHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws
            Exception {
        if (in.readableBytes() <= 4) {
            return;
        }

        int length = in.readInt();
        in.markReaderIndex();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
        } else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            RpcRequest znsRequest = JsonSerializerUtil.deserialize(bytes, RpcRequest.class);
            list.add(znsRequest);
        }
    }
}
