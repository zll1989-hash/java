package com.itcast.rpc.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class RpcServerConfiguration {

    @Value("${rpc.server.zk.root}")
    private String zkRoot;

    @Value("${rpc.server.zk.addr}")
    private String zkAddr;

    @Value("${rpc.server.zk.switch}")
    private boolean zkSwitch;

    @Value("${rpc.network.port}")
    private int networkPort;

    @Value("${server.port}")
    private int serverPort;

    @Value("${rpc.server.zk.timeout:10000}")
    private int connectTimeout;
}
