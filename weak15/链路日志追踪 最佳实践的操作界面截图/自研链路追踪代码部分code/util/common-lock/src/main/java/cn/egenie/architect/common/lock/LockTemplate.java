package cn.egenie.architect.common.lock;

import java.util.function.Supplier;

/**
 * @author lucien
 * @since 2021/06/18
 */
public interface LockTemplate {
    <T> T tryLockWithReturn(String key, Supplier<T> supplier);

    void tryLock(String key, Runnable runnable);

    <T> T lockWithReturn(String key, Supplier<T> supplier);

    void lock(String lockKey, Runnable runnable);
}
