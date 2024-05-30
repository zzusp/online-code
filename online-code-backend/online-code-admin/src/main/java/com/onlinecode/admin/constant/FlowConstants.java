package com.onlinecode.admin.constant;

import java.io.File;

/**
 * @author 孙鹏
 * @description 静态常量
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
public class FlowConstants {

    public static final String FLOW_PROCESS_DIR = System.getProperty("user.dir") + File.separator
            + ".flowprocesses" + File.separator;
    public static final String FLOW_PROCESS_SUFFIX = ".json";
    /**
     * 分布式业务锁key的前缀
     */
    public static final String BUSINESS_LOCK = "business_lock:";
    /**
     * 分布式业务数据缓存key的前缀
     */
    public static final String BUSINESS_CACHE = "business_cache:";

}
