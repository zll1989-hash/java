package com.itcast.rpc.server.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    private RpcServerConfiguration rpcServerConfiguration;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(rpcServerConfiguration.getZkAddr(), rpcServerConfiguration.getConnectTimeout());
    }
}
