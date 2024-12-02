package com.codeva.admin.sys.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface RedisLockService {

    /**
     * 拿锁执行，执行后自动释放锁
     *
     * @param key
     * @param excute  拿到锁后，执行的方法
     * @param tryTime 等待锁定的最长时间
     * @param unit
     * @return
     */
    void lock(String key, Consumer<Void> excute, long tryTime, TimeUnit unit);
}
