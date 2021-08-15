package cn.egenie.architect.trace.dubbo.filter;

import java.util.Arrays;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import cn.egenie.architect.common.core.exception.ServiceException;
import cn.egenie.architect.trace.collect.constants.MdcTraceConstants;
import cn.egenie.architect.trace.core.ConsumerContext;
import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.constants.TraceConstants;
import cn.egenie.architect.trace.core.enums.ServiceType;
import cn.egenie.architect.trace.core.manager.TraceManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/07
 */
@Activate(
        group = {"consumer"}
)
@Slf4j
public class TraceDubboConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Span span = TraceContext.peek();
        if (span == null) {
            return invoker.invoke(invocation);
        }
        else {
            String name = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();
            return TraceManager.tracingWithReturn(
                    ServiceType.DUBBO_CONSUMER,
                    name,
                    () -> {
                        ConsumerContext consumerContext = ConsumerContext.of(TraceContext.peek());
                        invocation.setAttachment(TraceConstants.CONSUMER_CONTEXT, consumerContext);

                        log.info("RequestParam: " + Arrays.toString(invocation.getArguments()));
                        Result result = invoker.invoke(invocation);
                        log.info("Response: " + result.get());

                        Throwable throwable = result.getException();
                        if (throwable != null) {
                            Span currentSpan = TraceContext.peek();
                            if (throwable instanceof ServiceException) {
                                ServiceException se = (ServiceException) throwable;
                                currentSpan.addError(se.getInterfacePath() + " : " + se.getExceptionClass());
                            }
                            else {
                                span.fillErrors(throwable);
                            }
                        }

                        return result;
                    },
                    MdcTraceConstants.MDC_RUNNABLE_LIST);
        }
    }
}
