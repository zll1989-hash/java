
/**
 * Rpc请求管理器
 */
public class RpcRequestManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestManager.class);

    private static final ConcurrentHashMap<String, ChannelHolder> channelHolderMap = new ConcurrentHashMap<>();

    private static final ExecutorService REQUEST_EXECUTOR = new ThreadPoolExecutor(
            30,
            100,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            new BasicThreadFactory.Builder().namingPattern("request-service-connector-%d").build()
    );

    private static RpcRequestPool ZNS_REQUEST_POOL;
    private static ServiceRouteCache SERVICE_ROUTE_CACHE;
    private static String CLUSTER_STRATEGY;

    public static void startRpcRequestManager(RpcRequestPool RpcRequestPool, ServiceRouteCache serviceRouteCache) {
        ZNS_REQUEST_POOL = RpcRequestPool;
        SERVICE_ROUTE_CACHE = serviceRouteCache;
        CLUSTER_STRATEGY = SpringBeanFactory.getBean(RpcClientConfiguration.class).getRpcClientClusterStrategy();
    }

    public static void sendRequest(RpcRequest rpcRequest) throws InterruptedException, RpcException {
        ClusterStrategy strategy = ClusterEngine.queryClusterStrategy(CLUSTER_STRATEGY);
        List<ProviderService> providerServices = SERVICE_ROUTE_CACHE.getServiceRoutes(rpcRequest.getClassName());
        ProviderService targetServiceProvider = strategy.select(providerServices);

        if (targetServiceProvider != null) {
            String requestId = rpcRequest.getRequestId();
            CountDownLatch latch = new CountDownLatch(1);
            REQUEST_EXECUTOR.execute(new RpcClientConnector(requestId, targetServiceProvider, latch));

            latch.await();

            ChannelHolder channelHolder = channelHolderMap.get(requestId);
            channelHolder.getChannel().writeAndFlush(rpcRequest);
            LOGGER.info("Send request[{}:{}] to service provider successfully", requestId, rpcRequest.toString());
        } else {
            throw new RpcException(StatusEnum.NOT_FOUND_SERVICE_PROVINDER);
        }
    }

    public static void registerChannelHolder(String requestId, ChannelHolder channelHolder) {
        if (StringUtils.isBlank(requestId) || channelHolder == null) {
            return;
        }
        channelHolderMap.put(requestId, channelHolder);
        LOGGER.info("Register ChannelHolder[{}:{}] successfully", requestId, channelHolder.toString());

        ZNS_REQUEST_POOL.submitRequest(requestId, channelHolder.getChannel().eventLoop());
        LOGGER.info("Submit request into RpcRequestPool successfully");
    }

    public static void destroyChannelHolder(String requestId) {
        if (StringUtils.isBlank(requestId)) {
            return;
        }
        ChannelHolder channelHolder = channelHolderMap.remove(requestId);
        try {
            channelHolder.getChannel().closeFuture();
            channelHolder.getEventLoopGroup().shutdownGracefully();
        } catch (Exception ex) {
            LOGGER.error("Close ChannelHolder[{}] error", requestId);
        }
        LOGGER.info("Destroy ChannelHolder[{}] successfully", requestId);
    }
}
