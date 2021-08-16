package cn.egenie.architect.trace.core.async;

import java.util.concurrent.Callable;

import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.manager.TraceManager;


/**
 * @author lucien
 * @since 2021/08/06 2021/01/09
 */
public class TraceCallable<V> implements Callable<V> {
    private Span asyncParent;
    private Callable<V> callable;


    public TraceCallable(Span asyncParent, Callable<V> callable) {
        this.asyncParent = asyncParent;
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        return TraceManager.asyncParent(this);
    }

    public static <V> Callable<V> getInstance(Callable<V> callable, String name) {
        if (callable == null) {
            return null;
        }

        Span asyncParent = Span.copyAsAsyncParent(TraceContext.peek(), name);
        return asyncParent == null ? callable : new TraceCallable<>(asyncParent, callable);
    }

    public Span getAsyncParent() {
        return asyncParent;
    }

    public Callable<V> getCallable() {
        return callable;
    }
}
