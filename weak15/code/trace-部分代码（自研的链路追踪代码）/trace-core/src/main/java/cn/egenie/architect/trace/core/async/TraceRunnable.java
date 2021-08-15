package cn.egenie.architect.trace.core.async;

import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.manager.TraceManager;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/09
 */
public class TraceRunnable implements Runnable {
    private Span asyncParent;
    private Runnable runnable;

    public TraceRunnable(Span asyncParent, Runnable runnable) {
        this.asyncParent = asyncParent;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        TraceManager.asyncParent(this);
    }

    public static Runnable getInstance(Runnable runnable, String name) {
        if (runnable == null) {
            return null;
        }

        Span asyncParent = Span.copyAsAsyncParent(TraceContext.peek(), name);
        return asyncParent == null ? runnable : new TraceRunnable(asyncParent, runnable);
    }

    public Span getAsyncParent() {
        return asyncParent;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
