package sqlrunner;

import com.alibaba.druid.pool.DruidDataSource;
import com.onlinecode.admin.process.dao.DefaultSqlRunner;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

public class SqlRunnerDemo {

    private TransactionFactory transactionFactory = new JdbcTransactionFactory();
    private DataSource dataSource = dataSource();
    private Properties pageProperties = pageProperties();

    public static void main(String[] args) {

        SqlRunnerDemo demo = new SqlRunnerDemo();
        DefaultSqlRunner sqlRunner = demo.openRunner();
        Map<String, Object> vars = new HashMap<>(8);
        vars.put("pageNum", 1);

        List<Map<String, Object>> nameList = sqlRunner.selectList("select DISTINCT reference_product from t_processes where middle_flow_id is null and reference_product is not null and is_deleted=0");
        if (nameList != null && nameList.size() > 0) {
            Set<String> nameSet = new HashSet<>();
            for (Map<String, Object> map : nameList) {
                String name = MapUtils.getString(map, "reference_product");
                if (StringUtils.isNotBlank(name)) {
                    nameSet.add(name);
                }
            }
            if (!nameSet.isEmpty()) {
                Map<String, Object> query = new HashMap<>(8);
                query.put("nameList", nameSet);
                List<Map<String, Object>> flows = sqlRunner.selectList("SELECT id, uuid, name from t_flows WHERE name IN " +
                        "(<foreach collection=\"nameList\" separator=\",\">#{item}</foreach>) AND is_deleted = 0\n" +
                        "        and flow_type = 'f4d1d8ab-e974-46a3-aa49-387029c473b'", query);
                System.out.println(flows);
                for (Map<String, Object> flow : flows) {
                    if (flow.getOrDefault("id", null) == null || flow.getOrDefault("name", null) == null) {
                        continue;
                    }
                    DefaultSqlRunner runner = demo.openRunner();
                    runner.update("update t_processes set middle_flow_id=#{id}, middle_flow_uuid=#{uuid}, middle_flow_name=#{name} where middle_flow_id is null and reference_product=#{name} and is_deleted=0;", flow);
                    runner.commit();
                    runner.close();
                }
            }
        }
//        System.out.println(nameList);
    }

    public DefaultSqlRunner openRunner() {
        return openRunner(null, false);
    }

    public DefaultSqlRunner openRunner(boolean autoCommit) {
        return openRunner(null, autoCommit);
    }

    public DefaultSqlRunner openRunner(TransactionIsolationLevel level, boolean autoCommit) {
        try {
            return new DefaultSqlRunner(transactionFactory.newTransaction(dataSource, level, autoCommit)
                    .getConnection(), pageProperties);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public DataSource dataSource() {
        try {
            // 创建DruidDataSource实例
            DruidDataSource dataSource = new DruidDataSource();

            // 设置数据源的基本属性
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://192.168.123.225:30306/hiq_lcd_data");
            dataSource.setUsername("root");
            dataSource.setPassword("test-pwd");

            // 可以设置更多的属性如初始连接数、最大连接数等

            // 初始化数据源
            dataSource.init();
            return dataSource;
        } catch (Exception e) {
            return null;
        }
    }

    public Properties pageProperties() {
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("defaultCount", "true");
        properties.setProperty("params", "count=countsql");
        return properties;
    }

}
