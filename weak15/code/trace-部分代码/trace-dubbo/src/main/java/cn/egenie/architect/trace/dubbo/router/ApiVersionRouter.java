package cn.egenie.architect.trace.dubbo.router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;

import cn.egenie.architect.common.core.util.Funs;
import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;

/**
 * @author lucien
 * @since 2021/08/06 2021/07/04
 */
public class ApiVersionRouter extends AbstractRouter {
    private static final String API_VERSION_PARAM = "apiVersion";
    private static final int TWO = 2;

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            return invokers;
        }

        Span span = TraceContext.peek();
        if (span == null) {
            return invokers;
        }

        int apiVersion = span.getApiVersion();
        Map<Integer, List<Invoker<T>>> invokersMap = Funs.groupingBy(invokers, invoker -> {
            String apiVersionStr = invoker.getUrl().getParameter(API_VERSION_PARAM);
            return StringUtils.isBlank(apiVersionStr) ? 0 : Integer.parseInt(apiVersionStr);
        });

        if (invokersMap.size() == 1) {
            return invokers;
        }

        List<Map.Entry<Integer, List<Invoker<T>>>> entryList = new ArrayList<>(invokersMap.entrySet());
        entryList.sort(Comparator.comparingInt(Map.Entry::getKey));

        Map.Entry<Integer, List<Invoker<T>>> lastEntry = entryList.get(entryList.size() - 1);
        if (apiVersion > lastEntry.getKey()) {
            return lastEntry.getValue();
        }

        // 在灰度发版中，如果最高version的provider已经占到一半或以上，就全部走最高version，避免低version的jvm被打垮
        if (invokers.size() > TWO) {
            if (lastEntry.getValue().size() >= invokers.size() / TWO) {
                return lastEntry.getValue();
            }
        }

        Map.Entry<Integer, List<Invoker<T>>> firstEntry = entryList.get(0);
        int lowestApiVersion = firstEntry.getKey();
        if (apiVersion < lowestApiVersion) {
            return firstEntry.getValue();
        }

        // 取第一个小于等于期望version的版本
        int firstEqualLessVersion = lowestApiVersion;
        for (int i = entryList.size() - 1; i >= 0; i--) {
            Map.Entry<Integer, List<Invoker<T>>> entry = entryList.get(i);
            if (entry.getKey() <= apiVersion) {
                firstEqualLessVersion = entry.getKey();
                break;
            }
        }

        return invokersMap.get(firstEqualLessVersion);
    }
}
