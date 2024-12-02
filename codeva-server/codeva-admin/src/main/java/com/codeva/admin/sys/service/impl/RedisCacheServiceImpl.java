package com.codeva.admin.sys.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.codeva.admin.sys.service.RedisCacheService;
import com.codeva.admin.sys.service.RedisLockService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

@Service
public class RedisCacheServiceImpl implements RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheServiceImpl.class);

    private final RedisLockService redisLockService;
    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisCacheServiceImpl(RedisLockService redisLockService, RedisTemplate<Object, Object> redisTemplate) {
        this.redisLockService = redisLockService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <T> T cache(String key, UnaryOperator<T> query, Class<T> type, long tryTime, long expireTime, TimeUnit unit) {
        // 先查缓存
        String cache = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNoneBlank(cache)) {
            return JSONObject.parseObject(cache, type);
        }
        // 缓存未找到，查询数据库
        T data = query.apply(null);
        // 分布式业务锁
        redisLockService.lock(key, v -> {
            try {
                // 写入缓存
                if (data == null) {
                    redisTemplate.delete(key);
                } else {
                    redisTemplate.opsForValue().setIfAbsent(key, JSONObject.toJSONString(data), expireTime, unit);
                }
            } catch (Exception e) {
                log.error("缓存数据失败，错误信息：{}", e.getMessage(), e);
            }
        }, tryTime, unit);
        return data;
    }

    @Override
    public <T> List<T> cacheList(String key, UnaryOperator<List<T>> query, Class<T> type, long tryTime, long expireTime, TimeUnit unit) {
        // 先查缓存
        String cache = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNoneBlank(cache)) {
            return JSONArray.parseArray(cache, type);
        }
        // 缓存未找到，查询数据库
        List<T> data = query.apply(null);
        // 分布式业务锁
        redisLockService.lock(key, v -> {
            try {
                // 写入缓存
                if (data == null) {
                    redisTemplate.delete(key);
                } else {
                    redisTemplate.opsForValue().setIfAbsent(key, JSONObject.toJSONString(data), expireTime, unit);
                }
            } catch (Exception e) {
                log.error("缓存数据失败，错误信息：{}", e.getMessage(), e);
            }
        }, tryTime, unit);
        return data;
    }
}
