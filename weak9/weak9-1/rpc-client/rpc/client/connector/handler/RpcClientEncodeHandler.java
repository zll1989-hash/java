

/**
 * Rpc客户端编码器
 */
public class RpcClientEncodeHandler extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest RpcRequest, ByteBuf in) throws Exception {
        byte[] bytes = JsonSerializerUtil.serialize(RpcRequest);
        in.writeInt(bytes.length);
        in.writeBytes(bytes);
    }
}
