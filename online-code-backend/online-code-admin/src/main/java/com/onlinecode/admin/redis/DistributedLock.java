package com.onlinecode.admin.redis;

import java.util.concurrent.TimeUnit;

public interface DistributedLock<T> {

    /**
     * 获取锁，默认30秒失效，失败一直等待，直到获取到锁
     *
     * @param key 锁的key
     * @return 锁对象
     */
    DLock<T> lock(String key);

    /**
     * 获取锁，失败一直等待，直到获取到锁
     *
     * @param key      锁的key
     * @param lockTime 加锁的时间，超过这个时间后，自动解锁；如果为-1，则保持锁定，直到手动解锁
     * @param unit     参数的时间单位
     * @param fair     是否公平锁
     * @return 锁对象
     */
    DLock<T> lock(String key, long lockTime, TimeUnit unit, boolean fair);

    /**
     * 获取锁，默认30秒失效，失败一直等待，直到获取到锁
     *
     * @param key     锁的key
     * @param tryTime 获取锁的最大尝试时间
     * @return 锁对象
     * @throws Exception 异常
     */
    DLock<T> tryLock(String key, long tryTime) throws Exception;

    /**
     * 获取锁，获取不到返回null
     *
     * @param key      锁的key
     * @param tryTime  获取锁的最大尝试时间
     * @param lockTime 加锁的时间，超过这个时间后，自动解锁；如果为-1，则保持锁定，直到手动解锁
     * @param unit     参数的时间单位
     * @param fair     是否公平锁
     * @return 锁对象
     * @throws Exception 异常
     */
    DLock<T> tryLock(String key, long tryTime, long lockTime, TimeUnit unit, boolean fair) throws Exception;

    /**
     * 解锁
     *
     * @param lock 锁对象
     */
    void unLock(T lock);

    /**
     * 解锁
     *
     * @param lock 锁对象
     */
    default void unLock(DLock<T> lock) {
        if (lock != null) {
            unLock(lock.getLock());
        }
    }

}
