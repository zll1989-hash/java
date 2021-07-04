package com.itcast.rpc.server.zk;

import com.itcast.common.annotation.RpcService;
import com.itcast.common.utils.IpUtil;
import com.itcast.common.utils.SpringBeanFactory;
import com.itcast.rpc.server.config.RpcServerConfiguration;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Zookeeper服务连接注册管理
 */
@Component
public class ServicePushManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePushManager.class);

    @Autowired
    private ZKit zKit;

    @Autowired
    private RpcServerConfiguration configuration;

    public void registerIntoZK() {
        Map<String, Object> beanWithAnnotations =
                SpringBeanFactory.getBeanListByAnnotationClass(RpcService.class);
        if (MapUtils.isEmpty(beanWithAnnotations)) {
            return;
        }

        zKit.createRootNode();
        for (Object bean : beanWithAnnotations.values()) {
            RpcService znsService = bean.getClass().getAnnotation(RpcService.class);
            String serviceName = znsService.cls().getName();
            pushServiceInfoIntoZK(serviceName);
        }
        LOGGER.info("Register service into zookeeper successfully");
    }

    private void pushServiceInfoIntoZK(String serviceName) {
        // Create persistent service node
        zKit.createPersistentNode(serviceName);

        String serviceAddress = IpUtil.getRealIp()
                + ":" + configuration.getServerPort()
                + ":" + configuration.getNetworkPort();
        String serviceAddressPath = serviceName + "/" + serviceAddress;
        zKit.createNode(serviceAddressPath);

        LOGGER.info("Register service[{}] into zookeeper successfully", serviceAddressPath);
    }
}
