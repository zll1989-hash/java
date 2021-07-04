
public class HashClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        String realIp = IpUtil.getRealIp();
        int hashCode = realIp.hashCode();

        int size = serviceRoutes.size();
        return serviceRoutes.get(hashCode % size);
    }
}
