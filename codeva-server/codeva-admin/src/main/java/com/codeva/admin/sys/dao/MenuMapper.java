package com.codeva.admin.sys.dao;

import com.codeva.admin.sys.model.SysMenu;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 孙鹏
 * @description 编排流程Mapper
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public interface MenuMapper {

    @Select("<script>SELECT id, code, name, parent_code, mode, type, new_tab, url, schema_json, sort, status, auth, " +
            "icon, create_time, create_by, update_time, update_by, remark, del_flag " +
            " FROM sys_menu WHERE del_flag='0'" +
            "</script>")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "code", property = "code"),
            @Result(column = "name", property = "name"),
            @Result(column = "parent_code", property = "parentCode"),
            @Result(column = "mode", property = "mode"),
            @Result(column = "type", property = "type"),
            @Result(column = "new_tab", property = "newTab"),
            @Result(column = "url", property = "url"),
            @Result(column = "schema_json", property = "schemaJson"),
            @Result(column = "sort", property = "sort"),
            @Result(column = "status", property = "status"),
            @Result(column = "auth", property = "auth"),
            @Result(column = "icon", property = "icon"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    List<SysMenu> getAllMenu();

    @Select("<script>SELECT id, code, name, parent_code, mode, type, new_tab, url, sort, status, auth, " +
            "icon, create_time, create_by, update_time, update_by, remark, del_flag " +
            " FROM sys_menu WHERE del_flag='0'" +
            "</script>")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "code", property = "code"),
            @Result(column = "name", property = "name"),
            @Result(column = "parent_code", property = "parentCode"),
            @Result(column = "mode", property = "mode"),
            @Result(column = "type", property = "type"),
            @Result(column = "new_tab", property = "newTab"),
            @Result(column = "url", property = "url"),
            @Result(column = "sort", property = "sort"),
            @Result(column = "status", property = "status"),
            @Result(column = "auth", property = "auth"),
            @Result(column = "icon", property = "icon"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    List<SysMenu> getAllMenuWithoutSchema();

}
