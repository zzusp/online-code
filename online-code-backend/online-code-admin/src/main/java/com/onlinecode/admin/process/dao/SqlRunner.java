package com.onlinecode.admin.process.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlRunner {

    private static final Logger log = LoggerFactory.getLogger(SqlRunner.class);

    private Connection connection;

    public SqlRunner(Connection connection) {
        this.connection = connection;
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void rollback() {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E> E execute(String sql, Map<String, Object> parameters, StatementHolder<PreparedStatement, E> function) {
        List<String> list = new ArrayList<>();
        // 变量替换成?
        int startIndex = sql.indexOf("#{");
        while (startIndex > 0) {
            int endIndex = sql.indexOf("}", startIndex);
            list.add(sql.substring(startIndex + 2, endIndex));
            sql = sql.substring(0, startIndex) + "?" + sql.substring(endIndex + 1);
            startIndex = sql.indexOf("#{");
        }
        Object[] arr = new Object[list.size()];
        if (!list.isEmpty()) {
            // 设置参数
            Object value;
            for (int i = 0; i < list.size(); i++) {
                value = null;
                String[] keyArr = list.get(i).split("\\.");
                int j = 0;
                while (j < keyArr.length) {
                    if (j == 0) {
                        value = parameters.get(keyArr[j]);
                    } else {
                        value = ((Map<String, Object>) value).get(keyArr[j]);
                    }
                    j++;
                }
                arr[i] = value;
            }
        }
        return execute(sql, arr, function);
    }

    private <E> E execute(String sql, Object[] parameters, StatementHolder<PreparedStatement, E> function) {
        boolean autoCommit = false;
        E rs = null;
        log.debug("==>  Preparing: {}", sql);
        List<String> parameterLog = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // 是否自动提交
            autoCommit = connection.getAutoCommit();
            // 设置参数
            if (parameters.length > 0) {
                int i = 0;
                while (i < parameters.length) {
                    statement.setObject(i + 1, parameters[i]);
                    // 日志中参数展示处理
                    String className = parameters[i].getClass().getName();
                    parameterLog.add(parameters[i] + "(" + className.substring(className.lastIndexOf(".") + 1) + ")");
                    i++;
                }
                log.debug("==> Parameters: {}", String.join(",", parameterLog));
            }
            // 执行sql
            rs = function.apply(statement);
        } catch (Exception e) {
            e.printStackTrace();
            // 提交失败，回滚事务
            rollback();
        } finally {
            // 自动提交事务时，需要自动关闭
            if (autoCommit) {
                close();
            }
        }
        return rs;
    }

    public List<Map<String, Object>> selectList(String sql) {
        return selectList(sql, null);
    }

    public List<Map<String, Object>> selectList(String sql, Map<String, Object> parameters) {
        return execute(sql, parameters,
                (statement -> {
                    List<Map<String, Object>> result = new ArrayList<>();
                    statement.execute();
                    ResultSet rs = statement.getResultSet();
                    ResultSetMetaData md = rs.getMetaData();
                    int columnCount = md.getColumnCount();
                    Map<String, Object> row;
                    // 循环遍历查询结果集
                    while (rs.next()) {
                        row = new HashMap<>(32);
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(md.getColumnName(i), rs.getObject(i));
                        }
                        result.add(row);
                    }
                    // 关闭结果集
                    rs.close();
                    return result;
                })
        );
    }

    public Map<String, Object> selectOne(String sql) {
        return selectList(sql).get(0);
    }

    public Map<String, Object> selectOne(String sql, Map<String, Object> parameters) {
        return selectList(sql, parameters).get(0);
    }

    public int insert(String sql, Map<String, Object> parameters) {
        return update(sql, parameters);
    }

    public int delete(String sql, Map<String, Object> parameters) {
        return update(sql, parameters);
    }

    public int update(String sql, Map<String, Object> parameters) {
        return execute(sql, parameters,
                (PreparedStatement::executeUpdate)
        );
    }

}
