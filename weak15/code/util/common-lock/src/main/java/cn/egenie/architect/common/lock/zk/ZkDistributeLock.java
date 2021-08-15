package cn.egenie.architect.common.lock.zk;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import cn.egenie.architect.common.core.util.Assert;
import cn.egenie.architect.common.lock.DistributeLock;

/**
 * @author lucien
 * @since 2021/03/01
 */
public class ZkDistributeLock implements DistributeLock {
    private InterProcessMutex lock;
    private String lockPath;

    public ZkDistributeLock(CuratorFramework client, String basePath, String key) {
        basePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        key = key.startsWith("/") ? key.substring(1) : key;
        this.lockPath = basePath + "/" + key;
        this.lock = new InterProcessMutex(client, this.lockPath);
    }

    public ZkDistributeLock(CuratorFramework client, String lockPath) {
        Assert.throwIfBlank(lockPath, "lockPath must not be blank");
        this.lockPath = lockPath;
        this.lock = new InterProcessMutex(client, lockPath);
    }

    @Override
    public void lock() {
        try {
            this.lock.acquire();
        }
        catch (Exception var2) {
            throw new IllegalStateException(var2.getMessage(), var2);
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        try {
            return this.lock.acquire(time, unit);
        }
        catch (Exception var5) {
            throw new IllegalStateException(var5.getMessage(), var5);
        }
    }

    @Override
    public void unlock() {
        try {
            this.lock.release();
        }
        catch (Exception var2) {
            throw new IllegalStateException(var2.getMessage(), var2);
        }
    }
}
