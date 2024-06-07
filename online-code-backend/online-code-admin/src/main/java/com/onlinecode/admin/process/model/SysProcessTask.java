package com.onlinecode.admin.process.model;

/**
 * @author 孙鹏
 * @description 编排流程节点实体
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public class SysProcessTask {

    private Long id;
    private String procCode;
    private String taskCode;
    private String taskName;
    private String executeCmd;

    public SysProcessTask() {
    }

    public SysProcessTask(Long id, String procCode, String taskCode, String taskName, String executeCmd) {
        this.id = id;
        this.procCode = procCode;
        this.taskCode = taskCode;
        this.taskName = taskName;
        this.executeCmd = executeCmd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExecuteCmd() {
        return executeCmd;
    }

    public void setExecuteCmd(String executeCmd) {
        this.executeCmd = executeCmd;
    }
}
