
/**
 *  随机权重
 */
public class WeightRandomClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        List<ProviderService> providerServices = Lists.newArrayList();
        for (ProviderService providerService : serviceRoutes) {
            int weight = providerService.getWeight();
            for (int i = 0; i < weight; i++) {
                providerServices.add(providerService);
            }
        }

        int MAX_LEN = providerServices.size();
        int index = RandomUtils.nextInt(0, MAX_LEN - 1);
        return providerServices.get(index);
    }
}
