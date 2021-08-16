package cn.egenie.architect.trace.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.service.GenericService;

import cn.egenie.architect.common.core.constants.Constants;
import cn.egenie.architect.common.core.exception.BusinessException;
import cn.egenie.architect.common.core.exception.ServiceException;
import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import lombok.extern.slf4j.Slf4j;


/**
 * @author lucien
 * @since 2021/08/06 2021/01/05
 */
@Activate(
        group = {"provider"}
)
@Slf4j
public class ServiceExceptionFilter implements Filter, Filter.Listener {
    public ServiceExceptionFilter() {
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = appResponse.getException();
                if (!(exception instanceof RuntimeException) && exception instanceof Exception) {
                    return;
                }

                log.error(invoker.getInterface().getName() + "." + invocation.getMethodName() + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);

                boolean isRpcException = exception instanceof RpcException;
                if (!isRpcException) {
                    // rpcException 需要返回调用方
                    // 其他异常消费者不关心提供者的错误堆栈
                    ServiceException serviceException = new ServiceException(exception.getMessage())
                            .setInterfacePath(invoker.getInterface().getCanonicalName() + "." + invocation.getMethodName())
                            .setExceptionClass(exception.getClass().getSimpleName());
                    serviceException.setCode(Constants.FAILED);

                    if (exception instanceof BusinessException) {
                        BusinessException bex = (BusinessException) exception;
                        serviceException.setCode(bex.getCode());
                        serviceException.setTrivial(bex.isTrivial());
                        serviceException.setDetailMsg(bex.getDetailMsg());
                    }

                    appResponse.setException(serviceException);

                    Span span = TraceContext.peek();
                    if (span != null) {
                        span.fillErrors(exception);
                    }
                }
            }
            catch (Throwable e) {
                log.warn(invoker.getInterface().getName() + "." + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
            }
        }

    }

    @Override
    public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
        log.error(invoker.getInterface().getName() + "." + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
    }
}
