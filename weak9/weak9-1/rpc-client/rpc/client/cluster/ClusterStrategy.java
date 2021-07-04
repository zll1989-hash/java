

/**
 * 集群调用策略接口
 */
public interface ClusterStrategy {

    ProviderService select(List<ProviderService> serviceRoutes);
}
