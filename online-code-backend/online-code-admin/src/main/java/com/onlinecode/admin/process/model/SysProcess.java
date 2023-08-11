package com.onlinecode.admin.process.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class SysProcess implements Serializable {

    private Long id;
    private String procCode;
    private String procName;
    private String bpmn;
    private String auth;
    private String status;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
    private String remark;
    private String delFlag;

    private List<SysProcessTask> tasks;

    public SysProcess(Long id, String procCode, String procName, String bpmn, String auth, String status,
                      LocalDateTime createTime, String createBy, LocalDateTime updateTime, String updateBy,
                      String remark) {
        this.id = id;
        this.procCode = procCode;
        this.procName = procName;
        this.bpmn = bpmn;
        this.auth = auth;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
        this.remark = remark;
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

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public String getBpmn() {
        return bpmn;
    }

    public void setBpmn(String bpmn) {
        this.bpmn = bpmn;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public List<SysProcessTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<SysProcessTask> tasks) {
        this.tasks = tasks;
    }
}
