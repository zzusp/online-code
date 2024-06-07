package com.onlinecode.admin.cron.dao;

import com.onlinecode.admin.cron.model.SysCron;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 孙鹏
 * @description 定时任务Mapper
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
public interface CronMapper {

    @Select("<script>SELECT id, cron_code, cron_name, cron_txt, proc_code, execute_param, execute_timeout, status," +
            " create_time, create_by, update_time, update_by, remark FROM sys_cron WHERE del_flag='0' " +
            "<if test=\"cronCode != null and cronCode != ''\"> AND cron_code = #{cronCode} </if>" +
            "<if test=\"cronName != null and cronName != ''\"> AND cron_name like concat(\"%\",#{cronName},\"%\") </if>" +
            "</script>")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "cron_code", property = "cronCode"),
            @Result(column = "cron_name", property = "cronName"),
            @Result(column = "cron_txt", property = "cronTxt"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "execute_param", property = "executeParam"),
            @Result(column = "execute_timeout", property = "executeTimeout"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    List<SysCron> getAllCron(@Param("cronCode") String cronCode, @Param("cronName") String cronName);

    @Select("SELECT id, cron_code, cron_name, cron_txt, proc_code, execute_param, execute_timeout, status, create_time, create_by," +
            " update_time, update_by, remark FROM sys_cron WHERE del_flag='0' AND id = #{id}")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "cron_code", property = "cronCode"),
            @Result(column = "cron_name", property = "cronName"),
            @Result(column = "cron_txt", property = "cronTxt"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "execute_param", property = "executeParam"),
            @Result(column = "execute_timeout", property = "executeTimeout"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    SysCron getById(@Param("id") Long id);

    @Select("SELECT id, cron_code, cron_name, cron_txt, proc_code, execute_param, execute_timeout, status, create_time, create_by," +
            " update_time, update_by, remark FROM sys_cron WHERE del_flag='0' AND cron_code = #{cronCode}")
    @Results(value = {
            @Result(column = "id", property = "id"),
            @Result(column = "cron_code", property = "cronCode"),
            @Result(column = "cron_name", property = "cronName"),
            @Result(column = "cron_txt", property = "cronTxt"),
            @Result(column = "proc_code", property = "procCode"),
            @Result(column = "execute_param", property = "executeParam"),
            @Result(column = "execute_timeout", property = "executeTimeout"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime", javaType = LocalDateTime.class),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_time", property = "updateTime", javaType = LocalDateTime.class),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "remark", property = "remark")
    })
    SysCron getByCronCode(@Param("cronCode") String cronCode);

    @Insert("INSERT INTO sys_cron(id, cron_code, cron_name, cron_txt, proc_code, execute_param, execute_timeout," +
            " status, create_time, create_by, update_time, update_by, remark, del_flag)" +
            " VALUES (#{id}, #{cronCode}, #{cronName}, #{cronTxt}, #{procCode}, #{executeParam}, #{executeTimeout}," +
            " #{status}, #{createTime}, #{createBy}, #{updateTime}, #{updateBy}, #{remark}, '0')")
    void insert(SysCron SysCron);

    @Update("UPDATE sys_cron SET cron_name = #{cronName}, cron_txt = #{cronTxt}, proc_code = #{procCode}," +
            " execute_param = #{executeParam}, execute_timeout = #{executeTimeout}, status = #{status}," +
            " update_time = #{updateTime}, update_by = #{updateBy}, remark = #{remark} WHERE id = #{id}")
    void update(SysCron sysCron);

    @Delete("DELETE FROM sys_cron WHERE id = #{id}")
    void delete(@Param("id") Long id);

}
