package cn.egenie.architect.common.lock.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import cn.egenie.architect.common.core.util.Assert;
import cn.egenie.architect.common.lock.DistributeLock;

/**
 * @author lucien
 * @since 2021/06/17
 */
public class RedisDistributeLock implements DistributeLock {
    private static final String DEFAULT_VALUE_REDIS = "default";

    private RedisTemplate<String, String> redisTemplate;
    private String key;

    public RedisDistributeLock(RedisTemplate<String, String> redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    @Override
    public void lock() {
        while (true) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, DEFAULT_VALUE_REDIS);
            if (success != null && success) {
                break;
            }
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        Assert.throwIfNull(unit, "unit must not be null");

        long start = System.currentTimeMillis();
        Long millisToWait = unit.toMillis(time);

        while (true) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, DEFAULT_VALUE_REDIS);
            if (success != null && success) {
                return true;
            }

            long now = System.currentTimeMillis();
            if (now - start > millisToWait) {
                return false;
            }
        }
    }

    @Override
    public void unlock() {
        redisTemplate.delete(key);
    }
}
