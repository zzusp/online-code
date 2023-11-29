package com.onlinecode.admin.process.dao;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Service
public class SqlRunnerFactory {

    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;
    private final Properties pageProperties;

    public SqlRunnerFactory(DataSource dataSource, @Qualifier("pageProperties") Properties pageProperties) {
        transactionFactory = new JdbcTransactionFactory();
        this.dataSource = dataSource;
        this.pageProperties = pageProperties;
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


}
