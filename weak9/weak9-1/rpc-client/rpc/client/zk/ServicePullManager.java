

/**
 * 注册服务拉取管理器
 */
@Component
public class ServicePullManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePullManager.class);

    @Autowired
    private ZKit zKit;

    @Autowired
    private ServiceRouteCache serviceRouteCache;

    @Autowired
    private RpcClientConfiguration configuration;

    public void pullServiceFromZK() {
        Reflections reflections = new Reflections(configuration.getRpcClientApiPackage());
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(RpcClient.class);
        if (CollectionUtils.isEmpty(typesAnnotatedWith)) {
            return;
        }
        for (Class<?> cls : typesAnnotatedWith) {
            String serviceName = cls.getName();

            // Cache service provider list into local
            List<ProviderService> providerServices = zKit.getServiceInfos(serviceName);
            serviceRouteCache.addCache(serviceName, providerServices);

            // Add listener for service node
            zKit.subscribeZKEvent(serviceName);
        }

        LOGGER.info("Pull service address list from zookeeper successfully");
    }
}
