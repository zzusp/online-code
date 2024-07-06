package com.onlinecode.admin.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙鹏
 * @description 状态枚举
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public enum StatusEnum {

    DISABLED("0", "禁用"),
    ENABLED("1", "启用");

    private final String code;
    private final String description;

    StatusEnum(String code, String description) {
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

    private static final Map<String, StatusEnum> LOCAL_MAP = new HashMap<>();

    static {
        for (StatusEnum e : values()) {
            LOCAL_MAP.put(e.getCode(), e);
        }
    }

    public static StatusEnum getByCode(String code) {
        return LOCAL_MAP.getOrDefault(code, null);
    }

    public static Boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return null != LOCAL_MAP.get(code);
    }

}
