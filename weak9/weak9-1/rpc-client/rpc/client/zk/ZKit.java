

@Component
public class ZKit {

    @Autowired
    private RpcClientConfiguration configuration;

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private ServiceRouteCache serviceRouteCache;

    /**
     * 服务订阅
     * @param serviceName
     */
    public void subscribeZKEvent(String serviceName) {
        String path = configuration.getZkRoot() + "/" + serviceName;
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> list) throws Exception {
                if (CollectionUtils.isNotEmpty(list)) {
                    List<ProviderService> providerServices = convertToProviderService(list);
                    serviceRouteCache.updateCache(serviceName, providerServices);
                }
            }
        });
    }

    public List<ProviderService> getServiceInfos(String serviceName) {
        String path = configuration.getZkRoot() + "/" + serviceName;
        List<String> children = zkClient.getChildren(path);

        List<ProviderService> providerServices = convertToProviderService(children);
        return providerServices;
    }

    private List<ProviderService> convertToProviderService(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayListWithCapacity(0);
        }
        List<ProviderService> providerServices = list.stream().map(v -> {
            String[] serviceInfos = v.split(":");
            return ProviderService.builder()
                    .serverIp(serviceInfos[0])
                    .serverPort(Integer.parseInt(serviceInfos[1]))
                    .networkPort(Integer.parseInt(serviceInfos[2]))
                    .build();
        }).collect(Collectors.toList());
        return providerServices;
    }
}
