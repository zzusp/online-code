package com.codeva.admin.sys.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 孙鹏
 * @description 菜单实体
 * @date Created in 17:26 2024/11/30
 * @modified By
 */
public class SysMenu implements Serializable {

    private Long id;
    private String code;
    private String name;
    private String parentCode;
    private String mode;
    private String type;
    private String newTab;
    private String url;
    private String schemaJson;
    private Integer sort;
    private String status;
    private String auth;
    private String icon;
    private LocalDateTime createTime;
    private String createBy;
    private LocalDateTime updateTime;
    private String updateBy;
    private String remark;
    private String delFlag;

    public SysMenu() {
    }

    public SysMenu(Long id, String code, String name, String parentCode, String mode, String type, String newTab,
                   String url, String schemaJson, Integer sort, String status, String auth, String icon,
                   LocalDateTime createTime, String createBy, LocalDateTime updateTime, String updateBy,
                   String remark, String delFlag) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
        this.mode = mode;
        this.type = type;
        this.newTab = newTab;
        this.url = url;
        this.schemaJson = schemaJson;
        this.sort = sort;
        this.status = status;
        this.auth = auth;
        this.icon = icon;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNewTab() {
        return newTab;
    }

    public void setNewTab(String newTab) {
        this.newTab = newTab;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSchemaJson() {
        return schemaJson;
    }

    public void setSchemaJson(String schemaJson) {
        this.schemaJson = schemaJson;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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
