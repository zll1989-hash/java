package cn.egenie.architect.trace.dubbo.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

/**
 * @author lucien
 * @since 2021/08/06 2021/07/04
 */
@Activate
public class ApiVersionRouterFactory implements RouterFactory {
    @Override
    public Router getRouter(URL url) {
        return new ApiVersionRouter();
    }
}
