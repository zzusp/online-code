package com.onlinecode.admin.sys.service.impl;

import java.util.Map;

public interface LoginService {

    /**
     * 获取用户角色及权限
     *
     * @param username 用户名
     * @return 角色和权限
     */
    Map<String, Object> getRoleAndPermission(String username);

}
