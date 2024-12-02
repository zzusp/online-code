package com.codeva.admin.constant;

public class RedisKey {

    /**
     * 分布式业务锁key的前缀
     */
    public static final String BUSINESS_LOCK = "codeva_lock:";
    /**
     * 分布式业务数据缓存key的前缀
     */
    public static final String BUSINESS_CACHE = "codeva_cache:";
    /**
     * 免登录可访问的process code的缓存key
     */
    public static final String ANON_PROC_CODES_CACHE = BUSINESS_CACHE + "anon_codes";
    /**
     * 登录即可访问的process code的缓存key
     */
    public static final String AUTH_PROC_CODES_CACHE = BUSINESS_CACHE + "auth_codes";

    public static final String PROC_CACHE_KEY = RedisKey.BUSINESS_CACHE + "sys_process:";

    public static final String ALL_PROC_CACHE = RedisKey.BUSINESS_CACHE + "all_process";

    public static final String ALL_MENU_CACHE = RedisKey.BUSINESS_CACHE + "all_menu";

    public static String getProcCacheKey(String procCode) {
        return PROC_CACHE_KEY + procCode;
    }
}
