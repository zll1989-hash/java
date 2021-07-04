package com.itcast.rpc.server.service;

import com.itcast.common.annotation.RpcService;
import com.itcast.common.utils.RpcException;
import com.itcast.rpc.api.OrderService;
import org.springframework.beans.factory.annotation.Value;

/**
 * <p>Description: </p>
 * @date 
 * @author 
 * @version 1.0
 * <p>Copyright:Copyright(c)2020</p>
 */
@RpcService(cls = OrderService.class)
public class OrderServiceImpl implements OrderService{

    @Value("${server.port}")
    private Integer serverPort;

    @Override
    public String getOrder(String userName, String orderNo) {
        if ("error".equalsIgnoreCase(userName)) {
            throw new RpcException("test exception! " + userName);
        }
        return String.format("Server(" + serverPort + "), Order Details => userName: %s, orderNo: %s", userName, orderNo);
    }
}
