package cn.egenie.architect.trace.dubbo.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.config.ApiVersionConfig;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import cn.egenie.architect.trace.collect.constants.MdcTraceConstants;
import cn.egenie.architect.trace.core.ConsumerContext;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.constants.TraceConstants;
import cn.egenie.architect.trace.core.enums.ServiceType;
import cn.egenie.architect.trace.core.manager.TraceManager;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/07
 */
@Activate(
        group = {"provider"}
)
public class TraceDubboProviderFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ConsumerContext consumerContext = (ConsumerContext) invocation.getObjectAttachment(TraceConstants.CONSUMER_CONTEXT);
        if (consumerContext == null) {
            return invoker.invoke(invocation);
        }
        else {
            String name = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();
            Map<String, String> tagMap = new HashMap<>(16);
            tagMap.put(TraceConstants.REQUEST_TAG_KEY, Arrays.toString(invocation.getArguments()));

            return TraceManager.tracingWithReturn(
                    consumerContext,
                    ServiceType.DUBBO_PROVIDER,
                    name,
                    tagMap,
                    () -> {
                        String actualApiVersion = ApiVersionConfig.getApiVersion();
                        TraceContext.peek().setActualApiVersion(StringUtils.isBlank(actualApiVersion) ? 0 : Integer.parseInt(actualApiVersion));
                        return invoker.invoke(invocation);
                    },
                    MdcTraceConstants.MDC_RUNNABLE_LIST);
        }
    }
}
