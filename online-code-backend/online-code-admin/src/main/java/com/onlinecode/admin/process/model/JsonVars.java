package com.onlinecode.admin.process.model;

import com.alibaba.fastjson2.JSONObject;
import com.onlinecode.admin.process.service.impl.ProcessServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙鹏
 * @description json vars
 * @date Created in 17:57 2024/6/14
 * @modified By
 */
public class JsonVars extends HashMap<String, Object> implements Map<String, Object> {

    private static final Logger log = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private static final long serialVersionUID = 1L;
    private static final String EMPTY_JSON = "{}";

    public static JsonVars parse(String str) {
        if (StringUtils.isBlank(str) || EMPTY_JSON.equals(str)) {
            return null;
        }
        try {
            return JSONObject.parseObject(str, JsonVars.class);
        } catch (Exception e) {
            log.error("parse json error: {}", str);
            return null;
        }
    }
}
