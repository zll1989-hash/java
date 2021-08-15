package cn.egenie.architect.common.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author lucien
 * @since 2021/03/01
 */
public interface DistributeLock {

    void lock();

    boolean tryLock(long time, TimeUnit unit);

    void unlock();
}
