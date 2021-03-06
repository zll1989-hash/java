
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;

import java.util.Date;

public class OutboundHandler1 extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        ByteBuf data = (ByteBuf) msg;
        System.out.println("OutboundHandler1 write : "+data.toString(CharsetUtil.UTF_8));
        ctx.write(Unpooled.copiedBuffer("OutboundHandler1 "+data.toString(CharsetUtil.UTF_8), CharsetUtil.UTF_8));
        ctx.flush();
    }
}
