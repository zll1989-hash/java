package com.itcast.rpc.server.connector.handler;

import com.itcast.common.data.RpcRequest;
import com.itcast.common.data.RpcResponse;
import com.itcast.common.utils.SpringBeanFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Rpc的服务端数据接收处理
 */
@Component
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest znsRequest) throws Exception {
        RpcResponse znsResponse = new RpcResponse();
        znsResponse.setRequestId(znsRequest.getRequestId());

        String className = znsRequest.getClassName();
        String methodName = znsRequest.getMethodName();
        Class<?>[] parameterTypes = znsRequest.getParameterTypes();
        Object[] parameterValues = znsRequest.getParameters();

        try {
            Object targetClass = SpringBeanFactory.getBean(Class.forName(className));
            Method targetMethod = targetClass.getClass().getMethod(methodName, parameterTypes);
            Object result = targetMethod.invoke(targetClass, parameterValues);
            znsResponse.setResult(result);
        } catch (Throwable cause) {
            znsResponse.setCause(cause);
        }
        ctx.writeAndFlush(znsResponse);
    }
}
