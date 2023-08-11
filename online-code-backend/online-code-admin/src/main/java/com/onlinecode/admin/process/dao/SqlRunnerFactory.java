package com.onlinecode.admin.process.dao;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

@Service
public class SqlRunnerFactory {

    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;

    public SqlRunnerFactory(DataSource dataSource) {
        transactionFactory = new JdbcTransactionFactory();
        this.dataSource = dataSource;
    }

    public SqlRunner openRunner() {
        return openRunner(null, false);
    }

    public SqlRunner openRunner(boolean autoCommit) {
        return openRunner(null, autoCommit);
    }

    public SqlRunner openRunner(TransactionIsolationLevel level, boolean autoCommit) {
        try {
            return new SqlRunner(transactionFactory.newTransaction(dataSource, level, autoCommit).getConnection());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


}
