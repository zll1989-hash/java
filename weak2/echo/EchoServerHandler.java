
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf data = (ByteBuf) msg;

        System.out.println("服务端收到数据: "+ data.toString(CharsetUtil.UTF_8));

        ctx.writeAndFlush(data);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        System.out.println("EchoServerHandle channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
