
@Component
@ChannelHandler.Sharable
public class RpcClientInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private RpcResponseHandler znsResponseHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new RpcClientEncodeHandler())
                .addLast(new RpcClientDecodeHandler())
                .addLast(znsResponseHandler);
    }
}
