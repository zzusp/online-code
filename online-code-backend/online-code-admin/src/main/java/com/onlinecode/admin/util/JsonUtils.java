package com.onlinecode.admin.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author 孙鹏
 * @description JSON工具类
 * @date Created in 18:22 2024/6/7
 * @modified By
 */
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public static Map<String, Object> convertJsonToMap(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("字符串转Map异常：{}", e.getMessage(), e);
            return null;
        }
    }

}
