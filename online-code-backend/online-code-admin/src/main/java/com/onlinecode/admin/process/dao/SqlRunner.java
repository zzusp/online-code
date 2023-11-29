package com.onlinecode.admin.process.dao;

import com.onlinecode.admin.web.page.PageTable;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface SqlRunner extends Closeable {

    List<Map<String, Object>> selectList(String sql);

    List<Map<String, Object>> selectList(String sql, Map<String, Object> parameters);

    List<Map<String, Object>> selectList(String sql, Map<String, Object> parameters, boolean camelCase);

    PageTable selectPage(String sql, int pageNum, int pageSize);

    PageTable selectPage(String sql, Map<String, Object> parameters);

    PageTable selectPage(String sql, Map<String, Object> parameters, boolean camelCase);

    Map<String, Object> selectOne(String sql);

    Map<String, Object> selectOne(String sql, Map<String, Object> parameters);

    Map<String, Object> selectOne(String sql, Map<String, Object> parameters, boolean camelCase);

    int insert(String sql, Map<String, Object> parameters);

    int update(String sql, Map<String, Object> parameters);

    int delete(String sql, Map<String, Object> parameters);

    void commit();

    void rollback();

    void close();

}
