package com.alibaba.compileflow.extension.util;

import com.alibaba.compileflow.engine.common.util.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class VarUtils extends com.alibaba.compileflow.engine.common.util.VarUtils {

    private Map<String, Object> vars;

    public VarUtils(Map<String, Object> vars) {
        this.vars = vars;
    }

    public Map<String, Object> getVars() {
        return vars;
    }

    public void setVars(Map<String, Object> vars) {
        this.vars = vars;
    }

    public String getString(String key) {
        if (MapUtils.isEmpty(vars) || !vars.containsKey(key) || vars.get(key) == null
                || StringUtils.isBlank(vars.get(key).toString())) {
            return null;
        }
        return vars.get(key).toString();
    }

    public Integer getInt(String key) {
        return getString(key) == null ? null : Integer.parseInt(vars.get(key).toString());
    }
}
