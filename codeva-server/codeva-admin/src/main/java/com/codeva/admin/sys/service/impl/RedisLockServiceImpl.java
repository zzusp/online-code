package com.codeva.admin.sys.service.impl;

import com.codeva.admin.constant.RedisKey;
import com.codeva.admin.exception.BusinessException;
import com.codeva.admin.sys.service.RedisLockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class RedisLockServiceImpl implements RedisLockService {
    private static final Logger log = LoggerFactory.getLogger(RedisLockServiceImpl.class);

    private final RedissonClient redissonClient;

    public RedisLockServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void lock(String key, Consumer<Void> excute, long tryTime, TimeUnit unit) {
        // 分布式业务锁
        RLock lock = redissonClient.getLock(RedisKey.BUSINESS_LOCK + key);
        // 获取锁
        boolean locked = false;
        try {
            // 获取锁
            locked = lock.tryLock(tryTime, unit);
        } catch (Exception e) {
            log.error("获取锁失败，错误信息：{}", e.getMessage(), e);
        }
        if (!locked) {
            throw new BusinessException("服务器忙，请稍后重试");
        }
        try {
            // 业务操作
            excute.accept(null);
        } finally {
            lock.unlock();
        }
    }
}
