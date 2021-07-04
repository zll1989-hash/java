
/**
 * Rpc数据接收响应处理器
 */
@Component
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Autowired
    private RpcRequestPool requestPool;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse znsResponse) throws Exception {
        requestPool.notifyRequest(znsResponse.getRequestId(), znsResponse);
    }
}
