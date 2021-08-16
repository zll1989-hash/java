package cn.egenie.architect.trace.core.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.egenie.architect.common.function.ThrowCallable;
import cn.egenie.architect.common.function.ThrowRunnable;
import cn.egenie.architect.trace.core.ConsumerContext;
import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContainer;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.async.TraceCallable;
import cn.egenie.architect.trace.core.async.TraceRunnable;
import cn.egenie.architect.trace.core.async.TraceSupplier;
import cn.egenie.architect.trace.core.constants.TraceConstants;
import cn.egenie.architect.trace.core.enums.ServiceType;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/06
 */
public class TraceManager {

    /**
     * 微服务提供者入口
     */
    public static <T> T tracingWithReturn(ConsumerContext consumerContext,
                                          ServiceType serviceType,
                                          String name,
                                          Map<String, String> tagMap,
                                          ThrowCallable<T> callable,
                                          List<Runnable> mdcRunnableList) {
        startSpan(consumerContext, serviceType, name, tagMap, mdcRunnableList);
        return invoke(callable, mdcRunnableList);
    }

    private static void startSpan(ConsumerContext consumerContext,
                                  ServiceType serviceType,
                                  String name,
                                  Map<String, String> tagMap,
                                  List<Runnable> mdcRunnableList) {
        Span span = Span.of(consumerContext, serviceType, name, tagMap);
        TraceContext.push(span);

        pushMDC(mdcRunnableList);
    }

    /**
     * Web端入口或者内部调用
     */
    public static <T> T tracingWithReturn(ServiceType serviceType,
                                          String name,
                                          ThrowCallable<T> callable,
                                          List<Runnable> mdcRunnableList) {
        startSpan(serviceType, name, mdcRunnableList);
        return invoke(callable, mdcRunnableList);
    }

    /**
     * 记录sql入口
     */
    public static <T> T tracingWithReturn(ServiceType serviceType,
                                          String name,
                                          Map<String, String> tagMap,
                                          ThrowCallable<T> callable,
                                          List<Runnable> mdcRunnableList) {
        startSpan(serviceType, name, tagMap, mdcRunnableList);
        return invoke(callable, mdcRunnableList);
    }

    private static void startSpan(ServiceType serviceType, String name, Map<String, String> tagMap, List<Runnable> mdcRunnableList) {
        Span parentSpan = TraceContext.peek();
        if (parentSpan == null) {
            parentSpan = TraceConstants.DUMMY_SPAN;

        }

        Span span = Span.of(parentSpan, serviceType, name, tagMap);
        TraceContext.push(span);

        pushMDC(mdcRunnableList);
    }

    private static <T> T invoke(ThrowCallable<T> callable, List<Runnable> mdcRunnableList) {
        try {
            T result = callable.call();
            Span span = TraceContext.peek();
            String serviceType = span.getServiceType();
            if (ServiceType.JDBC.message().equals(serviceType)) {
                if (result instanceof Integer || result instanceof Long) {
                    span.putTag(TraceConstants.SQL_RESULT_TOTAL_TAG_KEY, String.valueOf(result));

                }
                else if (result instanceof Collection<?>) {
                    Collection<?> collection = (Collection<?>) result;
                    span.putTag(TraceConstants.SQL_RESULT_TOTAL_TAG_KEY, String.valueOf(collection.size()));
                }
            }

            return result;
        }
        catch (Throwable e) {
            fillError(e);
            throw buildException(e);
        }
        finally {
            TraceManager.endSpan();
            clearMDC(mdcRunnableList);
        }
    }

    /**
     * Web端入口
     */
    public static void tracing(ServiceType serviceType,
                               String name,
                               ThrowRunnable runnable,
                               List<Runnable> mdcRunnableList) {
        startSpan(serviceType, name, mdcRunnableList);
        invoke(runnable, mdcRunnableList);
    }


    private static void startSpan(ServiceType serviceType, String name, List<Runnable> mdcRunnableList) {
        Span parentSpan = TraceContext.peek();
        if (parentSpan == null) {
            parentSpan = TraceConstants.DUMMY_SPAN;

        }

        Span span = Span.of(parentSpan, serviceType, name, null);
        TraceContext.push(span);

        pushMDC(mdcRunnableList);
    }

    private static void invoke(ThrowRunnable runnable, List<Runnable> mdcRunnableList) {
        try {
            runnable.run();
        }
        catch (Throwable e) {
            fillError(e);
            throw buildException(e);
        }
        finally {
            TraceManager.endSpan();
            clearMDC(mdcRunnableList);
        }
    }

    private static RuntimeException buildException(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        else {
            return new RuntimeException(e);
        }
    }

    private static void fillError(Throwable e) {
        Span currentSpan = TraceContext.peek();
        if (currentSpan != null) {
            currentSpan.fillErrors(e);
        }
    }

    public static void endSpan() {
        Span span = TraceContext.pop();
        if (span.isAsyncParent()) {
            List<Span> children = span.getChildren();
            if (children == null || children.isEmpty()) {
                return;
            }
        }

        TraceContainer.getInstance().put(span);
    }

    private static void pushMDC(List<Runnable> mdcRunnableList) {
        mdcRunnableList.get(0).run();
    }

    private static void clearMDC(List<Runnable> mdcRunnableList) {
        if (TraceContext.isEmpty()) {
            mdcRunnableList.get(1).run();
        }
    }


    public static void asyncParent(TraceRunnable traceRunnable) {
        TraceContext.push(traceRunnable.getAsyncParent());
        try {
            traceRunnable.getRunnable().run();
        }
        catch (Throwable e) {
            throw buildException(e);
        }
        finally {
            TraceManager.endSpan();
        }
    }


    public static <V> V asyncParent(TraceCallable<V> traceCallable) {
        TraceContext.push(traceCallable.getAsyncParent());
        try {
            return traceCallable.getCallable().call();
        }
        catch (Throwable e) {
            throw buildException(e);
        }
        finally {
            TraceManager.endSpan();
        }
    }

    public static <T> T asyncParent(TraceSupplier<T> traceSupplier) {
        TraceContext.push(traceSupplier.getAsyncParent());
        try {
            return traceSupplier.getSupplier().get();
        }
        catch (Throwable e) {
            throw buildException(e);
        }
        finally {
            TraceManager.endSpan();
        }
    }
}
