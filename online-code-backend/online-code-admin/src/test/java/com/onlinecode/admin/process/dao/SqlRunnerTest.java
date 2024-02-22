package com.onlinecode.admin.process.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinecode.admin.web.page.PageTable;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlRunnerTest {

    @Autowired
    private SqlRunnerFactory sqlRunnerFactory;

    @Test
    public void selectList() {

        DefaultSqlRunner sqlRunner = sqlRunnerFactory.openRunner();
//        Map<String, Object> params = new HashMap<>(8);
//        params.put("biz_tag", "leaf-segment-test");
//        List<Map<String, Object>> list = sqlRunner.selectList("SELECT biz_tag, max_id, step, update_time FROM leaf_alloc WHERE biz_tag=#{biz_tag}", params);
//        Map<String, Object> insertData = new HashMap<>(8);
//        insertData.put("biz_tag", "sys_process_task");
//        insertData.put("max_id", 1);
//        insertData.put("step", 2000);
//        insertData.put("description", "流程表主键1");
//        insertData.put("update_time", new Date());
//        int num = sqlRunner.insert("INSERT INTO leaf_alloc(biz_tag, max_id, step, description, update_time) VALUES (#{biz_tag}, #{max_id}, #{step}, #{description}, #{update_time})", insertData);
//        Map<String, Object> updateData = new HashMap<>(8);
//        updateData.put("biz_tag", "sys_process_task");
//        updateData.put("step", "sys_process_task");
//        insertData.put("update_time", new Date());
//        // 测试报错自动回滚
//        num = sqlRunner.update("UPDATE leaf_alloc SET step = #{step}, update_time = #{update_time} WHERE biz_tag = #{biz_tag}", updateData);

//        List<Map<String, Object>> roles = sqlRunner.selectList("SELECT role_id FROM sys_user_role WHERE user_id=#{id}", user);
//        List<String> roleIds = roles.stream().map(v -> v.get("roleId").toString()).collect(Collectors.toList());

        Map<String, Object> vars = new HashMap<>(8);
        vars.put("pageNum", 1);
        vars.put("pageSize", 1);
        vars.put("userName", "1323");
        String sql = "SELECT"
                + " id, user_name, nick_name, email, avatar, password, locked, status, create_time, create_by, update_time, update_by"
                + " FROM sys_user WHERE del_flag='0'";
//        ;
        if (StringUtils.isNotEmpty(MapUtils.getString(vars, "userName"))) {
            sql += " AND user_name like concat(\"%\",#{userName},\"%\")";
        }
//        if (StringUtils.isNotEmpty(MapUtils.getString(vars, "nickName"))) {
//            sql += " AND nick_name like concat(\"%\",#{nickName},\"%\")";
//        }
//        // 分页
//        PageHelper.startPage((int) vars.getOrDefault("pageNum", 1), (int) vars.getOrDefault("pageSize", 0));
//        // 查询
//        List<Map<String, Object>> list = sqlRunner.selectList(sql, vars, true);
//        Map<String, Long> procMap =  list.stream().map(v -> v.get("menu_code").toString()).collect(Collectors.groupingBy(v -> v, Collectors.counting()));
//        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
//        sqlRunner.close();
//        // 设置返回结果
//        vars.put("flowRes", PageTable.page(pageInfo.getTotal(), pageInfo.getList()));
//        sqlRunner.selectPage(sql, vars, true);
//        sqlRunner.commit();
//        sqlRunner.close();
        List<String> roleIds = new ArrayList<>();
        roleIds.add("1");
        roleIds.add("2");
        roleIds.add("3");
        Map<String, Object> query = new HashMap<>(8);
        query.put("roleIds", roleIds);
        List<Map<String, Object>> menus = sqlRunner.selectList("SELECT sm.code, sm.auth, sm.url FROM sys_menu sm " +
                "LEFT JOIN sys_role_menu srm ON sm.id=srm.menu_id WHERE srm.role_id IN " +
                "(<foreach collection=\"roleIds\" separator=\",\">#{item}</foreach>) AND sm.del_flag='0'", query);
        System.out.println(menus);
        // 流程
        List<Map<String, Object>> process = sqlRunner.selectList("SELECT sp.proc_code, sp.menu_code, sp.auth " +
                "FROM sys_process sp LEFT JOIN sys_role_process srp ON sp.id=srp.proc_id "
                + "WHERE srp.role_id IN (<foreach collection=\"roleIds\" separator=\",\">#{item}</foreach>) AND sp.del_flag='0'", query, true);
        System.out.println(process);


    }

    @Test
    public void insertBatch() {
        String sql = "INSERT INTO sys_user_role(user_id,role_id) VALUES <foreach collection=\"list\" separator=\",\">(#{list.userId},#{list.id})</foreach>";
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> vars = new HashMap<>(8);
            vars.put("userId", i+1);
            vars.put("id", i);
            list.add(vars);
        }
        DefaultSqlRunner sqlRunner = sqlRunnerFactory.openRunner();
        sqlRunner.insertBatch(sql, list, "list", 5);
//        System.out.println(sqlRunner.matchForeach(sql, vars));;
        sqlRunner.commit();
    }
}
