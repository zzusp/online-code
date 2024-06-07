package com.onlinecode.admin.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙鹏
 * @description ID枚举
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public enum IdKeyEnum {

    SYS_PROCESS("sys_process", "流程信息表"),
    SYS_CRON("sys_cron", "定时任务表");

    private final String code;
    private final String description;

    IdKeyEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean equals(String code) {
        return this.code.equals(code);
    }

    private static final Map<String, IdKeyEnum> LOCAL_MAP = new HashMap<>();

    static {
        for (IdKeyEnum e : values()) {
            LOCAL_MAP.put(e.getCode(), e);
        }
    }

    public static IdKeyEnum getByCode(String code) {
        return LOCAL_MAP.getOrDefault(code, null);
    }

    public static Boolean isValid(String code) {
        if (code != null && !code.isEmpty()) {
            return false;
        }
        return null != LOCAL_MAP.get(code);
    }

}
