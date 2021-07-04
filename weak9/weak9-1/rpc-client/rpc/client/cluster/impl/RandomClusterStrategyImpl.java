
public class RandomClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        int MAX_LEN = serviceRoutes.size();
        int index = RandomUtils.nextInt(0, MAX_LEN - 1);
        return serviceRoutes.get(index);
    }
}
