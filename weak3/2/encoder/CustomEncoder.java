
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomEncoder extends MessageToByteEncoder<Integer> {

    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
            out.writeInt(msg);
    }


}
