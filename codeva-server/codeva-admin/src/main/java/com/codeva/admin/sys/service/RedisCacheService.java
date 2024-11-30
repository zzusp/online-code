package com.codeva.admin.sys.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public interface RedisCacheService {

    /**
     * 缓存查，查不到执行方法，并将结果放入缓存
     *
     * @param key
     * @param query      缓存找不到时，执行的方法
     * @param tryTime    上锁时长
     * @param expireTime 过期时长
     * @param unit
     * @param <T>
     * @return
     */
    <T> T cache(String key, UnaryOperator<T> query, Class<T> type, long tryTime, long expireTime, TimeUnit unit);

    <T> List<T> cacheList(String key, UnaryOperator<List<T>> query, Class<T> type, long tryTime, long expireTime, TimeUnit unit);
}
