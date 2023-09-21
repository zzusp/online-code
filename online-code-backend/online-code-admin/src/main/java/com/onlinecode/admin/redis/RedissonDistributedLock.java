package com.onlinecode.admin.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedissonDistributedLock implements DistributedLock<RLock> {

    private static final Logger log = LoggerFactory.getLogger(RedissonDistributedLock.class);

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public DLock<RLock> lock(String key) {
        return this.lock(key, 0L, TimeUnit.SECONDS, false);
    }

    @Override
    public DLock<RLock> lock(String key, long lockTime, TimeUnit unit, boolean fair) {
        RLock lock = this.getLock(key, fair);
        // 获取锁，失败一直等待，直到获取锁，不支持自动续期
        if (lockTime > 0) {
            lock.lock(lockTime, unit);
        } else {
            // 具有watch dog自动续期机制，默认续30s，每个30/3=10s，续到30s
            lock.lock();
        }
        return new DLock<>(lock, this);
    }

    @Override
    public DLock<RLock> tryLock(String key, long tryTime) throws Exception {
        return this.tryLock(key, tryTime, 0L, TimeUnit.SECONDS, false);
    }

    @Override
    public DLock<RLock> tryLock(String key, long tryTime, long lockTime, TimeUnit unit, boolean fair) throws Exception {
        RLock lock = this.getLock(key, fair);
        boolean lockAcquired;
        if (lockTime > 0) {
            lockAcquired = lock.tryLock(tryTime, lockTime, unit);
        } else {
            lockAcquired = lock.tryLock(tryTime, unit);
        }

        if (lockAcquired) {
            return new DLock<>(lock, this);
        }
        return null;
    }

    private RLock getLock(String key, boolean fair) {
        return fair ? redissonClient.getFairLock(key) : redissonClient.getLock(key);
    }

    @Override
    public void unLock(RLock lock) {
        if (lock.isLocked()) {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.error("释放分布式锁异常", e);
            }
        }
    }
}
