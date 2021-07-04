

/**
 * Rpc客户端连接器
 */
public class RpcClientConnector implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientConnector.class);

    private String requestId;
    private ProviderService providerService;
    private CountDownLatch latch;
    private RpcClientInitializer znsClientInitializer;

    public RpcClientConnector(String requestId, ProviderService providerService, CountDownLatch latch) {
        this.requestId = requestId;
        this.providerService = providerService;
        this.latch = latch;
        this.znsClientInitializer = SpringBeanFactory.getBean(RpcClientInitializer.class);
    }

    @Override
    public void run() {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(providerService.getServerIp(), providerService.getNetworkPort())
                .handler(znsClientInitializer);

        try {
            ChannelFuture future = bootstrap.connect().sync();
            if (future.isSuccess()) {
                ChannelHolder channelHolder = ChannelHolder.builder()
                        .channel(future.channel())
                        .eventLoopGroup(worker)
                        .build();
                RpcRequestManager.registerChannelHolder(requestId, channelHolder);
                LOGGER.info("Construct a connector with service provider[{}:{}] successfully",
                        providerService.getServerIp(),
                        providerService.getNetworkPort()
                );

                latch.countDown();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
