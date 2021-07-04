

/**
 * 动态代理管理器
 */
@Component
public class ServiceProxyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProxyManager.class);

    @Autowired
    private RpcClientConfiguration configuration;

    @Autowired
    private ProxyHelper proxyHelper;

    public void initServiceProxyInstance() {
        Reflections reflections = new Reflections(configuration.getRpcClientApiPackage());
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(RpcClient.class);
        if (CollectionUtils.isEmpty(typesAnnotatedWith)) {
            return;
        }

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) SpringBeanFactory.context()
                .getAutowireCapableBeanFactory();
        for (Class<?> cls : typesAnnotatedWith) {
            RpcClient znsClient = cls.getAnnotation(RpcClient.class);
            String serviceName = cls.getName();
            beanFactory.registerSingleton(serviceName, proxyHelper.newProxyInstance(cls));
        }

        LOGGER.info("Initialize proxy for service successfully");
    }
}
