package com.itcast.rpc.api;

import com.itcast.common.annotation.RpcClient;

/**
 * <p>Description: </p>
 * @date 
 * @author 
 * @version 1.0
 * <p>Copyright:Copyright(c)2020</p>
 */
@RpcClient
public interface OrderService {

    /**
     * 获取订单信息
     * @param userName
     * @param goodsName
     * @return
     */
    String getOrder(String userName, String orderNo);
}