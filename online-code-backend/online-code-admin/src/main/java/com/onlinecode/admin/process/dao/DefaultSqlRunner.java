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
    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("separator=\"(.*?)\"");
    private static final Pattern COLLECTION_PATTERN = Pattern.compile("collection=\"(.*?)\"");

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

    private <E> E execute(String sql, Map<String, Object> parameters, StatementHolder<PreparedStatement, E> function) {
        Object[] arr = new Object[0];
        if (parameters != null && !parameters.isEmpty()) {
            Map<String, Object> runParameters = new HashMap<>(parameters);
            List<String> list = new ArrayList<>();
            sql = matchForeach(sql, runParameters);
            sql = matchKey(sql, list);
            arr = matchParameter(list, runParameters);
        }
        return execute(sql, arr, function);
    }

    private String matchKey(String sql, List<String> list) {
        // 变量替换成?
        int startIndex = sql.indexOf("#{");
        while (startIndex >= 0) {
            int endIndex = sql.indexOf("}", startIndex);
            list.add(sql.substring(startIndex + 2, endIndex));
            sql = sql.substring(0, startIndex) + "?" + sql.substring(endIndex + 1);
            startIndex = sql.indexOf("#{");
        }
        return sql;
    }

    @SuppressWarnings("unchecked")
    public String matchForeach(String sql, Map<String, Object> parameters) {
        String foreach;
        Matcher separatorMatcher;
        String separator;
        Matcher collectionMatcher;
        String collection;
        List<String> strList;
        StringBuilder sb;
        List<Map<String, Object>> paramList;
        int forStart = sql.indexOf("<foreach");
        while (forStart >= 0) {
            int forEnd = sql.indexOf("</foreach>");
            int lastStart = sql.substring(0, forEnd).lastIndexOf("<foreach");

            foreach = sql.substring(lastStart, forEnd);
            // 分隔符
            separatorMatcher = SEPARATOR_PATTERN.matcher(foreach);
            separator = separatorMatcher.find() ? separatorMatcher.group(0)
                    .replace("separator=\"", "").replace("\"", "") : ",";
            // 集合key
            collectionMatcher = COLLECTION_PATTERN.matcher(foreach);
            if (!collectionMatcher.find()) {
                throw new SQLErrorException("SQL表达式异常：foreach标签缺少collection属性");
            }
            collection = collectionMatcher.group(0).replace("collection=\"", "")
                    .replace("\"", "");
            if (!parameters.containsKey(collection)) {
                throw new SQLErrorException("SQL参数异常：缺少参数 " + collection);
            }
            if (!(parameters.get(collection) instanceof Collection)) {
                throw new SQLErrorException("SQL参数异常：参数" + collection + " 应为集合类型");
            }

            int tabStart = foreach.indexOf(">");
            String tabInner = foreach.substring(tabStart + 1);
            strList = new ArrayList<>();
            paramList = new ArrayList<>((Collection<Map<String, Object>>) parameters.get(collection));
            for (int i = 0; i < paramList.size(); i++) {
                String template = tabInner;
                int startIndex = template.indexOf("#{");
                while (startIndex >= 0) {
                    int endIndex = template.indexOf("}", startIndex);
                    String key = template.substring(startIndex + 2, endIndex);
                    // 设置for前缀
                    String forPrefix = "for:" + i + ":";
                    if (!key.contains(".")) {
                        String newKey = forPrefix + collection + ":" + key;
                        parameters.put(newKey, paramList.get(i));
                        // 设置新的key
                        template = template.replaceFirst("#\\{" + key + "}", "#\\{" + newKey + "}");
                    } else if (key.startsWith(collection + ".")) {
                        // 设置新key对应的参数
                        Map<String, Object> sub = new HashMap<>(8);
                        if (parameters.containsKey(forPrefix + collection)) {
                            sub = (Map<String, Object>) parameters.get(forPrefix + collection);
                        }
                        sub.put(key.split("\\.")[1], paramList.get(i).get(key.split("\\.")[1]));
                        parameters.put(forPrefix + collection, sub);
                        // 设置新的key
                        template = template.replaceFirst("#\\{" + key + "}", "#\\{" + forPrefix + key + "}");
                    }
                    startIndex = template.indexOf("#{", endIndex);
                }
                strList.add(template);
            }
            sql = sql.replace(foreach + "</foreach>", String.join(separator, strList));
            forStart = sql.indexOf("<foreach");
        }
        return sql;
    }

    @SuppressWarnings("unchecked")
    private Object[] matchParameter(List<String> list, Map<String, Object> parameters) {
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
        return arr;
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
    public int insertBatch(String sql, List<Map<String, Object>> parameters, String collection, int batchSize) {
        if (parameters == null || parameters.isEmpty()) {
            return 0;
        }
        Map<String, Object> vars = new HashMap<>(8);
        if (batchSize <= 0) {
            vars.put(collection, parameters);
            return execute(sql, vars, (PreparedStatement::executeUpdate));
        }
        // 分批次提交执行
        int num = parameters.size() % batchSize > 0 ? parameters.size() / batchSize + 1 : parameters.size() / batchSize;
        for (int i = 0; i < num; i++) {
            vars.put(collection, parameters.subList(i * batchSize, Math.min((i + 1) * batchSize,
                    parameters.size())));
            execute(sql, vars, (PreparedStatement::executeUpdate));
        }
        return 0;
    }

    @Override
    public int update(String sql, Map<String, Object> parameters) {
        return execute(sql, parameters, (PreparedStatement::executeUpdate));
    }

    @Override
    public int delete(String sql, Map<String, Object> parameters) {
        return execute(sql, parameters, (PreparedStatement::executeUpdate));
    }

    /**
     * 下划线转驼峰
     *
     * @param str 字符串
     * @return 替换结果
     */
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
