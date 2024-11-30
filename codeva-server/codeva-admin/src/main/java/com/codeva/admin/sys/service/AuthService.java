package com.codeva.admin.sys.service;

import com.codeva.admin.sys.model.SysProcess;

import java.util.List;

public interface AuthService {

    boolean checkMenuPermission(String menuCode);

    boolean checkProcessPermission(String procCode, List<SysProcess> list);

}
