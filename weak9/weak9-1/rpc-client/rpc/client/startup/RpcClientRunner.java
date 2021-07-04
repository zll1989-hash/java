
/**
 * Rpc客户端启动实现
 */
@Component
public class RpcClientRunner {

    @Autowired
    private ServicePullManager servicePullManager;

    @Autowired
    private ServiceProxyManager serviceProxyManager;

    @Autowired
    private RpcRequestPool rpcRequestPool;

    @Autowired
    private ServiceRouteCache serviceRouteCache;

    public void run() {
        // Start request manager
        RpcRequestManager.startRpcRequestManager(rpcRequestPool, serviceRouteCache);

        // Pull service provider info from zookeeper
        servicePullManager.pullServiceFromZK();

        // Create proxy for service which owns @ZnsClient annotation
        serviceProxyManager.initServiceProxyInstance();
    }
}
