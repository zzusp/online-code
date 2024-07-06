package com.onlinecode.admin.cron.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 孙鹏
 * @description 定时任务实体类
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public class SysCron implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String cronCode;
    private String cronName;
    private String cronTxt;
    private String procCode;
    private String executeParam;
    private String executeTimeout;
    private String status;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
    private String remark;
    private String delFlag;

    public SysCron() {
    }

    public SysCron(Long id, String cronCode, String cronName, String cronTxt, String procCode, String executeParam,
                   String executeTimeout, String status, LocalDateTime createTime, String createBy,
                   LocalDateTime updateTime, String updateBy, String remark, String delFlag) {
        this.id = id;
        this.cronCode = cronCode;
        this.cronName = cronName;
        this.cronTxt = cronTxt;
        this.procCode = procCode;
        this.executeParam = executeParam;
        this.executeTimeout = executeTimeout;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
        this.remark = remark;
        this.delFlag = delFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCronCode() {
        return cronCode;
    }

    public void setCronCode(String cronCode) {
        this.cronCode = cronCode;
    }

    public String getCronName() {
        return cronName;
    }

    public void setCronName(String cronName) {
        this.cronName = cronName;
    }

    public String getCronTxt() {
        return cronTxt;
    }

    public void setCronTxt(String cronTxt) {
        this.cronTxt = cronTxt;
    }

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getExecuteParam() {
        return executeParam;
    }

    public void setExecuteParam(String executeParam) {
        this.executeParam = executeParam;
    }

    public String getExecuteTimeout() {
        return executeTimeout;
    }

    public void setExecuteTimeout(String executeTimeout) {
        this.executeTimeout = executeTimeout;
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
}
