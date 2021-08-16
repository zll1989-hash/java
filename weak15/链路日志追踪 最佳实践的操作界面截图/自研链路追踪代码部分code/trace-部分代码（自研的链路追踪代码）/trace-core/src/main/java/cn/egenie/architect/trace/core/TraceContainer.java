package cn.egenie.architect.trace.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lucien
 * @since 2021/08/06 2021/01/06
 */
public class TraceContainer {

    private AtomicInteger failCounter;
    private BlockingQueue<Span> queue;
    private volatile boolean isActive = true;

    private TraceContainer() {
        this.queue = new ArrayBlockingQueue<>(1024 * 8);
        this.failCounter = new AtomicInteger(0);
    }


    private static final TraceContainer instance = new TraceContainer();

    public static TraceContainer getInstance() {
        return instance;
    }

    public void put(Span span) {
        if (!this.isActive || span == null) {
            return;
        }

        if (span.isCollected()) {
            return;
        }

        Span parent = span.getParent();
        if (parent != null && parent.isCollected()) {
            return;
        }

        offer(span);
    }

    private void offer(Span span) {
        if (!queue.offer(span)) {
            failCounter.incrementAndGet();
        }
        span.setCollected(true);
    }


    public BlockingQueue<Span> getQueue() {
        return queue;
    }

    public int getAndResetFailCounter() {
        return failCounter.getAndSet(0);
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return this.isActive;
    }
}
