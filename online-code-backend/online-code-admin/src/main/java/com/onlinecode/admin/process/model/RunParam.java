package com.onlinecode.admin.process.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 孙鹏
 * @description 执行参数
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public class RunParam implements Serializable {

    private static final long serialVersionUID = 7829125842083645635L;

    private String procCode;
    private String taskCode;
    private String cmd;
    private Map<String, Object> vars;

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Map<String, Object> getVars() {
        return vars;
    }

    public void setVars(Map<String, Object> vars) {
        this.vars = vars;
    }
}
