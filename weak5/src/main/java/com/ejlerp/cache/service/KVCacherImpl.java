package com.ejlerp.cache.service;

import com.ejlerp.cache.api.KVCacher;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

@DubboService(version = "0.1")
public class KVCacherImpl implements KVCacher {

    private final StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> opt;

    public KVCacherImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.opt = stringRedisTemplate.opsForValue();
    }

    @Override
    public boolean del(String key) {
        if (!stringRedisTemplate.hasKey(key)) {
            return false;
        }
        stringRedisTemplate.delete(key);
        return true;
    }

    @Override
    public String get(String key) {
        return opt.get(key);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        opt.set(key, value, timeout, unit);
    }
}
