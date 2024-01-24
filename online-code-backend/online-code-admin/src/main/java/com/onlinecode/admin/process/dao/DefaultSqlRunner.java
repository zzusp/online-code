package com.onlinecode.admin.process.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.parser.CountSqlParser;
import com.onlinecode.admin.exception.SQLErrorException;
import com.onlinecode.admin.web.page.PageTable;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultSqlRunner implements SqlRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultSqlRunner.class);

    private Connection connection;
    private Properties pageProperties;

    public DefaultSqlRunner(Connection connection, Properties pageProperties) {
        this.connection = connection;
        this.pageProperties = pageProperties;
    }

    @Override
    public void commit() {
        try {
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
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
                Object obj;
                while (i < parameters.length) {
                    obj = parameters[i];
                    statement.setObject(i + 1, obj);
                    if (obj != null) {
                        // 日志中参数展示处理
                        String className = obj.getClass().getName();
                        parameterLog.add(obj + "(" + className.substring(className.lastIndexOf(".") + 1) + ")");
                    } else {
                        parameterLog.add("null");
                    }
                    i++;
                }
                log.debug("==> Parameters: {}", String.join(",", parameterLog));
            }
            // 执行sql
            rs = function.apply(statement);
        } catch (Exception e) {
            // 提交失败，回滚事务
            rollback();
            throw new SQLErrorException("SQL执行失败，错误信息：" + e.getMessage(), e.getCause());
        } finally {
            // 自动提交事务时，需要自动关闭
            if (autoCommit) {
                close();
            }
        }
        return rs;
    }

    @Override
    public List<Map<String, Object>> selectList(String sql) {
        return selectList(sql, null);
    }

    @Override
    public List<Map<String, Object>> selectList(String sql, Map<String, Object> parameters) {
        return selectList(sql, parameters, false);
    }

    @Override
    public List<Map<String, Object>> selectList(String sql, Map<String, Object> parameters, boolean camelCase) {
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
                            // 列名是否下划线转驼峰
                            if (camelCase) {
                                row.put(underlineToCamel(md.getColumnLabel(i)), rs.getObject(i));
                            } else {
                                row.put(md.getColumnLabel(i), rs.getObject(i));
                            }
                        }
                        result.add(row);
                    }
                    // 关闭结果集
                    rs.close();
                    return result;
                })
        );
    }

    @Override
    public PageTable selectPage(String sql, int pageNum, int pageSize) {
        Map<String, Object> parameters = new HashMap<>(4);
        parameters.put("pageNum", pageNum);
        parameters.put("pageSize", pageSize);
        return selectPage(sql, parameters);
    }

    @Override
    public PageTable selectPage(String sql, Map<String, Object> parameters) {
        return selectPage(sql, parameters, false);
    }

    @Override
    public PageTable selectPage(String sql, Map<String, Object> parameters, boolean camelCase) {
        Integer pageNum = MapUtils.getInteger(parameters, "pageNum");
        Integer pageSize = MapUtils.getInteger(parameters, "pageSize");
        if (pageNum == null || pageNum < 1 || pageSize == null || pageSize < 1) {
            log.warn("Please set page param first");
            return PageTable.empty();
        }
        String countSql = new CountSqlParser().getSimpleCountSql(sql);
        PageHelper dialect = new PageHelper();
        dialect.setProperties(pageProperties);
        String pageSql = dialect.getPageSql(sql, new Page<>(pageNum, pageSize), new RowBounds(), null);
        Map<String, Object> count = this.selectOne(countSql, parameters);
        // 替换分页参数占位符为变量
        if (pageSql.split("\\?").length == 3) {
            pageSql = pageSql.replaceFirst("\\?", "#{pageNum}");
        }
        pageSql = pageSql.replaceFirst("\\?", "#{pageSize}");
        List<Map<String, Object>> page = this.selectList(pageSql, parameters, camelCase);
        return PageTable.page(Long.parseLong(count.get("count(0)").toString()), page);
    }

    @Override
    public Map<String, Object> selectOne(String sql) {
        List<Map<String, Object>> list = selectList(sql);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Map<String, Object> selectOne(String sql, Map<String, Object> parameters) {
        List<Map<String, Object>> list = selectList(sql, parameters);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Map<String, Object> selectOne(String sql, Map<String, Object> parameters, boolean camelCase) {
        List<Map<String, Object>> list = selectList(sql, parameters, camelCase);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int insert(String sql, Map<String, Object> parameters) {
        return execute(sql, parameters, (PreparedStatement::executeUpdate));
    }

    @Override
    public int update(String sql, Map<String, Object> parameters) {
        return execute(sql, parameters, (PreparedStatement::executeUpdate));
    }

    @Override
    public int delete(String sql, Map<String, Object> parameters) {
        return execute(sql, parameters, (PreparedStatement::executeUpdate));
    }

    public static String underlineToCamel(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        Pattern pattern = Pattern.compile("_(\\w)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer result = new StringBuffer();
        while (matcher.find())
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        matcher.appendTail(result);
        return result.toString();
    }
}
