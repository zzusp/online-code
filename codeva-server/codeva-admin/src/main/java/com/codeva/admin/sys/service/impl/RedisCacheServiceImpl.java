package com.codeva.admin.sys.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.codeva.admin.constant.RedisKey;
import com.codeva.admin.exception.BusinessException;
import com.codeva.admin.sys.service.RedisCacheService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

@Service
public class RedisCacheServiceImpl implements RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);

    private final RedissonClient redissonClient;
    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisCacheServiceImpl(RedissonClient redissonClient, RedisTemplate<Object, Object> redisTemplate) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <T> T cache(String key, UnaryOperator<T> query, Class<T> type, long tryTime, long expireTime, TimeUnit unit) {
        // 先查缓存
        String cache = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNoneBlank(cache)) {
            return (T) JSONObject.parseObject(cache, type);
        }
        // 缓存未找到，查询数据库
        T data = query.apply(null);
        // 分布式业务锁
        RLock lock = redissonClient.getLock(RedisKey.BUSINESS_LOCK + key);
        try {
            // 获取锁
            boolean locked = lock.tryLock(tryTime, unit);
            if (!locked) {
                throw new BusinessException("服务器忙，请稍后重试");
            }
            // 写入缓存
            if (data == null) {
                redisTemplate.delete(key);
            } else {
                redisTemplate.opsForValue().setIfAbsent(key, JSONObject.toJSONString(data), expireTime, unit);
            }
        } catch (Exception e) {
            log.error("缓存数据失败，错误信息：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        return data;
    }

    @Override
    public <T> List<T> cacheList(String key, UnaryOperator<List<T>> query, Class<T> type, long tryTime, long expireTime, TimeUnit unit) {
        // 先查缓存
        String cache = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNoneBlank(cache)) {
            return (List<T>) JSONArray.parseArray(cache, type);
        }
        // 缓存未找到，查询数据库
        List<T> data = query.apply(null);
        // 分布式业务锁
        RLock lock = redissonClient.getLock(RedisKey.BUSINESS_LOCK + key);
        try {
            // 获取锁
            boolean locked = lock.tryLock(tryTime, unit);
            if (!locked) {
                throw new BusinessException("服务器忙，请稍后重试");
            }
            // 写入缓存
            if (data == null) {
                redisTemplate.delete(key);
            } else {
                redisTemplate.opsForValue().setIfAbsent(key, JSONObject.toJSONString(data), expireTime, unit);
            }
        } catch (Exception e) {
            log.error("缓存数据失败，错误信息：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        return data;
    }
}
