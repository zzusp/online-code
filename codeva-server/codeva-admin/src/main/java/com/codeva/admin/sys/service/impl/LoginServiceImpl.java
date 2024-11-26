package com.codeva.admin.sys.service.impl;

import com.codeva.admin.db.DefaultSqlRunner;
import com.codeva.admin.db.session.SqlRunnerFactory;
import com.codeva.admin.sys.service.LoginService;
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
