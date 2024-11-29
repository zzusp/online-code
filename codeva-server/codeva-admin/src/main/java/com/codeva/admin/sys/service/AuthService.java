package com.codeva.admin.sys.service;

public interface AuthService {

    boolean checkMenuPermission(String menuCode);

    boolean checkProcessPermission(String procCode);

}
