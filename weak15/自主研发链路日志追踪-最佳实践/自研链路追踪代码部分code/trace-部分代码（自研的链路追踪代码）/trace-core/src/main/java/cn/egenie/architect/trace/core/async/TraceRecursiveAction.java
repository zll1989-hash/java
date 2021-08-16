package cn.egenie.architect.trace.core.async;

import java.util.concurrent.ForkJoinTask;

import cn.egenie.architect.trace.core.Span;
import cn.egenie.architect.trace.core.TraceContext;
import cn.egenie.architect.trace.core.manager.TraceManager;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/11
 */
public abstract class TraceRecursiveAction extends ForkJoinTask<Void> {
    private Span asyncParent;

    public TraceRecursiveAction() {
        asyncParent = Span.copyAsAsyncParent(TraceContext.peek(), "ForkJoinTask.doExec");
    }

    @Override
    public Void getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(Void value) {

    }

    /**
     * The main computation performed by this task.
     */
    protected abstract void compute();

    @Override
    protected boolean exec() {
        if (asyncParent == null) {
            compute();
            return true;
        }
        else {
            TraceContext.push(asyncParent);
            try {
                compute();
                return true;
            }
            finally {
                TraceManager.endSpan();
            }
        }
    }
}
