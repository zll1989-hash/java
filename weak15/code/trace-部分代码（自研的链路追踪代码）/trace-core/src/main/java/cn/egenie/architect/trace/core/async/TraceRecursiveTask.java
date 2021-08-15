package cn.egenie.architect.trace.core.async;

import java.util.concurrent.ForkJoinTask;

import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.manager.TraceManager;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/11
 */
public abstract class TraceRecursiveTask<T> extends ForkJoinTask<T> {
    private Span asyncParent;
    private T result;


    public TraceRecursiveTask() {
        asyncParent = Span.copyAsAsyncParent(TraceContext.peek(), "ForkJoinTask.doExec");
    }


    /**
     * The main computation performed by this task.
     */
    protected abstract T compute();

    @Override
    protected boolean exec() {
        if (asyncParent == null) {
            result = compute();
            return true;
        }
        else {
            TraceContext.push(asyncParent);
            try {
                result = compute();
                return true;
            }
            finally {
                TraceManager.endSpan();
            }
        }
    }

    @Override
    public final T getRawResult() {
        return result;
    }

    @Override
    protected final void setRawResult(T value) {
        result = value;
    }
}
