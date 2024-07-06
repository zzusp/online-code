package com.onlinecode.admin.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙鹏
 * @description 鉴权类型枚举
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public enum AuthTypeEnum {

    ANON("0", "匿名访问"),
    AUTH("1", "需要登录"),
    PERMS("2", "需要授权");

    private final String code;
    private final String description;

    AuthTypeEnum(String code, String description) {
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

    private static final Map<String, AuthTypeEnum> LOCAL_MAP = new HashMap<>();

    static {
        for (AuthTypeEnum e : values()) {
            LOCAL_MAP.put(e.getCode(), e);
        }
    }

    public static AuthTypeEnum getByCode(String code) {
        return LOCAL_MAP.getOrDefault(code, null);
    }

    public static Boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return null != LOCAL_MAP.get(code);
    }

}
