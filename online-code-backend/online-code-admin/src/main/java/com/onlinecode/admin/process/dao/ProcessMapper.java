package com.onlinecode.admin.process.dao;

import com.onlinecode.admin.process.model.SysProcess;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ProcessMapper {

    @Select("SELECT id, proc_code, proc_name, bpmn, auth, status, create_time, create_by, update_time, update_by," +
            " remark FROM sys_process WHERE del_flag='0'")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "proc_name", property = "procName"),
            @Result(column = "bpmn", property = "bpmn"),
            @Result(column = "auth", property = "auth"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    List<SysProcess> getAllProcess();

    @Select("SELECT id, proc_code, proc_name, bpmn, auth, status, create_time, create_by, update_time, update_by," +
            " remark FROM sys_process WHERE del_flag='0' AND id = #{id}")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "proc_name", property = "procName"),
            @Result(column = "bpmn", property = "bpmn"),
            @Result(column = "auth", property = "auth"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    SysProcess getById(@Param("id") Long id);

    @Select("SELECT id, proc_code, proc_name, bpmn, auth, status, create_time, create_by, update_time, update_by," +
            " remark FROM sys_process WHERE del_flag='0' AND proc_code = #{procCode}")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "proc_name", property = "procName"),
            @Result(column = "bpmn", property = "bpmn"),
            @Result(column = "auth", property = "auth"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    SysProcess getByProcCode(@Param("procCode") String procCode);

    @Insert("INSERT INTO sys_process(id, proc_code, proc_name, bpmn, auth, status, create_time, create_by," +
            " update_time, update_by, remark, del_flag)" +
            " VALUES (#{id}, #{procCode}, #{procName}, #{bpmn}, #{auth}, #{status}, #{createTime}, #{createBy}," +
            " #{updateTime}, #{updateBy}, #{remark}, '0')")
    void insert(SysProcess sysProcess);

    @Update("UPDATE sys_process SET proc_name = #{procName}, bpmn = #{bpmn}, auth = #{auth}, status = #{status}," +
            " update_time = #{updateTime}, update_by = #{updateBy}, remark = #{remark} WHERE id = #{id}")
    void update(SysProcess sysProcess);

    @Delete("DELETE FROM sys_process WHERE id = #{id}")
    void delete(@Param("id") Long id);

}
