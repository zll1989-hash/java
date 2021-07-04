
public class PollingClusterStrategyImpl implements ClusterStrategy {

    private int counter = 0;
    private Lock lock = new ReentrantLock();

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        ProviderService providerService = null;
        try {
            lock.tryLock(10, TimeUnit.SECONDS);
            int size = serviceRoutes.size();
            if (counter >= size) {
                counter = 0;
            }

            providerService = serviceRoutes.get(counter);
            counter++;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }

        if (providerService == null) {
            providerService = serviceRoutes.get(0);
        }
        return providerService;
    }
}
