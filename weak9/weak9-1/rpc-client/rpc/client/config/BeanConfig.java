
@Configuration
public class BeanConfig {

    private static final int EXPIRE_SECONDS = 86400;

    @Autowired
    private RpcClientConfiguration configuration;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(configuration.getZkAddr(), configuration.getConnectTimeout());
    }

    @Bean
    public LoadingCache<String, List<ProviderService>> buildCache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, List<ProviderService>>() {
                    @Override
                    public List<ProviderService> load(String key) throws Exception {
                        return null;
                    }
                });
    }
}
