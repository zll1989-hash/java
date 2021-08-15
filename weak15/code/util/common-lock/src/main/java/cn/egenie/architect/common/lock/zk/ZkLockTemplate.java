package cn.egenie.architect.common.lock.zk;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.egenie.architect.common.core.exception.BusinessException;
import cn.egenie.architect.common.lock.LockTemplate;

/**
 * @author lucien
 * @since 2021/03/01
 */
public class ZkLockTemplate implements LockTemplate {
    @Autowired
    private CuratorFramework curatorClient;

    /**
     * 推荐使用 "/" + appKey + "/dist_lock/"
     */
    @Value("${zklock.base.path")
    private String basePath;

    @Override
    public <T> T tryLockWithReturn(String lockKey, Supplier<T> supplier) {
        ZkDistributeLock lock = new ZkDistributeLock(curatorClient, basePath, lockKey);

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
    public void tryLock(String lockKey, Runnable runnable) {
        ZkDistributeLock lock = new ZkDistributeLock(curatorClient, basePath, lockKey);

        if (lock.tryLock(1, TimeUnit.MILLISECONDS)) {
            try {
                runnable.run();
            }
            finally {
                lock.unlock();
            }
        }
        else {
            throw new BusinessException("系统正在处理，请稍后再重试:" + lockKey);
        }
    }

    @Override
    public <T> T lockWithReturn(String lockKey, Supplier<T> supplier) {
        ZkDistributeLock lock = new ZkDistributeLock(curatorClient, basePath, lockKey);

        lock.lock();
        try {
            return supplier.get();
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void lock(String lockKey, Runnable runnable) {
        ZkDistributeLock lock = new ZkDistributeLock(curatorClient, basePath, lockKey);

        lock.lock();
        try {
            runnable.run();
        }
        finally {
            lock.unlock();
        }
    }
}
