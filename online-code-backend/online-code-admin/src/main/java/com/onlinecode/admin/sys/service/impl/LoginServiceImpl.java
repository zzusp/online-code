package com.onlinecode.admin.sys.service.impl;

import com.onlinecode.admin.process.dao.DefaultSqlRunner;
import com.onlinecode.admin.process.dao.SqlRunnerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    private final SqlRunnerFactory sqlRunnerFactory;

    public LoginServiceImpl(SqlRunnerFactory sqlRunnerFactory) {
        this.sqlRunnerFactory = sqlRunnerFactory;
    }

    @Override
    public Map<String, Object> getRoleAndPermission(String username) {
        DefaultSqlRunner sqlRunner = sqlRunnerFactory.openRunner();

        return null;
    }
}
