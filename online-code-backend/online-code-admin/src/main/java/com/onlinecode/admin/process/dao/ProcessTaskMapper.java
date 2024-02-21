package com.onlinecode.admin.process.dao;

import com.onlinecode.admin.process.model.SysProcessTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ProcessTaskMapper {

    @Select("SELECT id, proc_code, task_code, task_name, execute_cmd FROM sys_process_task")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "task_code", property = "taskCode"),
            @Result(column = "task_name", property = "taskName"),
            @Result(column = "execute_cmd", property = "executeCmd")
    })
    List<SysProcessTask> getAllTask();

    @Select("SELECT id, proc_code, task_code, task_name, execute_cmd FROM sys_process_task" +
            " WHERE proc_code = #{procCode}")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "task_code", property = "taskCode"),
            @Result(column = "task_name", property = "taskName"),
            @Result(column = "execute_cmd", property = "executeCmd")
    })
    List<SysProcessTask> getByProcCode(@Param("procCode") String procCode);


    @Select("SELECT id, proc_code, task_code, task_name, execute_cmd FROM sys_process_task" +
            " WHERE proc_code = #{procCode}")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "task_code", property = "taskCode"),
            @Result(column = "task_name", property = "taskName"),
            @Result(column = "execute_cmd", property = "executeCmd")
    })
    SysProcessTask getByProcCodeAndTaskCode(@Param("procCode") String procCode, @Param("taskCode") String taskCode);

    @Insert("<script>" +
            "INSERT INTO sys_process_task(id, proc_code, task_code, task_name, execute_cmd)" +
            " VALUES " +
            " <foreach collection='list' item='item' separator=','> " +
            "  (#{item.id}, #{item.procCode}, #{item.taskCode}, #{item.taskName}, #{item.executeCmd})" +
            " </foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<SysProcessTask> list);

    @Delete("DELETE FROM sys_process_task WHERE proc_code = #{procCode}")
    void deleteByProcCode(@Param("procCode") String procCode);

}
