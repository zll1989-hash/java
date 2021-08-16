package cn.egenie.architect.common.lock.redis;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import cn.egenie.architect.common.core.exception.BusinessException;
import cn.egenie.architect.common.lock.DistributeLock;
import cn.egenie.architect.common.lock.LockTemplate;

/**
 * @author lucien
 * @since 2021/06/17
 */
public class RedisLockTemplate implements LockTemplate {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public <T> T tryLockWithReturn(String key, Supplier<T> supplier) {
        DistributeLock lock = new RedisDistributeLock(redisTemplate, key);

        if (lock.tryLock(1, TimeUnit.MILLISECONDS)) {
            try {
                return supplier.get();
            }
            finally {
                lock.unlock();
            }
        }
        else {
            throw new BusinessException("系统正在处理，请稍后再重试");
        }
    }

    @Override
    public void tryLock(String key, Runnable runnable) {
        DistributeLock lock = new RedisDistributeLock(redisTemplate, key);

        if (lock.tryLock(1, TimeUnit.MILLISECONDS)) {
            try {
                runnable.run();
            }
            finally {
                lock.unlock();
            }
        }
        else {
            throw new BusinessException("系统正在处理，请稍后再重试");
        }
    }

    @Override
    public <T> T lockWithReturn(String key, Supplier<T> supplier) {
        DistributeLock lock = new RedisDistributeLock(redisTemplate, key);

        lock.lock();
        try {
            return supplier.get();
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void lock(String key, Runnable runnable) {
        DistributeLock lock = new RedisDistributeLock(redisTemplate, key);

        lock.lock();
        try {
            runnable.run();
        }
        finally {
            lock.unlock();
        }
    }
}
