package com.codeva.admin.sys.service.impl;

import com.codeva.admin.sys.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public boolean checkMenuPermission(String menuCode) {
        return false;
    }

    @Override
    public boolean checkProcessPermission(String procCode) {
        return false;
    }
}
