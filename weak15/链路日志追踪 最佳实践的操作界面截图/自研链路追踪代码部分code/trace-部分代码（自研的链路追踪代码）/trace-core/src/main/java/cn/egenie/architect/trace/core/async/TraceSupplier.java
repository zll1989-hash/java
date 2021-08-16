package cn.egenie.architect.trace.core.async;

import java.util.function.Supplier;

import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.manager.TraceManager;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/11
 */
public class TraceSupplier<T> implements Supplier<T> {
    private Span asyncParent;
    private Supplier<T> supplier;

    public TraceSupplier(Span asyncParent, Supplier<T> supplier) {
        this.asyncParent = asyncParent;
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return TraceManager.asyncParent(this);
    }

    public static <T> Supplier<T> getInstance(Supplier<T> supplier, String name) {
        if (supplier == null) {
            return null;
        }

        Span asyncParent = Span.copyAsAsyncParent(TraceContext.peek(), name);
        return asyncParent == null ? supplier : new TraceSupplier<>(asyncParent, supplier);
    }

    public Span getAsyncParent() {
        return asyncParent;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }
}
