package com.itcast.rpc.server.startup;

import com.itcast.rpc.server.connector.RpcServerAcceptor;
import com.itcast.rpc.server.zk.ServicePushManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Rpc服务端启动实现
 */
@Component
public class RpcServerRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerRunner.class);

    private static ExecutorService executor = null;

    @Autowired
    private ServicePushManager servicePushManager;

    public void run() {
        executor = Executors.newFixedThreadPool(5);

        // Start Acceptor，waiting for the service caller to fire the request call
        executor.execute(new RpcServerAcceptor());

        // Register service providers into Zookeeper
        servicePushManager.registerIntoZK();
    }


    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
