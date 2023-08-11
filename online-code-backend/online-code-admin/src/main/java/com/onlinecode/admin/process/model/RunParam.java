package com.onlinecode.admin.process.model;

import java.util.Map;

public class RunParam {

    private String procCode;
    private Map<String, Object> vars;

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public Map<String, Object> getVars() {
        return vars;
    }

    public void setVars(Map<String, Object> vars) {
        this.vars = vars;
    }
}
