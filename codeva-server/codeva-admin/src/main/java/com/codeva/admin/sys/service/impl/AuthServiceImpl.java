package com.codeva.admin.sys.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.codeva.admin.constant.ProcConstants;
import com.codeva.admin.enums.AuthTypeEnum;
import com.codeva.admin.enums.StatusEnum;
import com.codeva.admin.exception.UnauthorizedException;
import com.codeva.admin.sys.model.SysMenu;
import com.codeva.admin.sys.model.SysProcess;
import com.codeva.admin.sys.service.AuthService;
import com.codeva.admin.sys.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final MenuService menuService;

    public AuthServiceImpl(MenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public boolean checkMenuPermission(String menuCode) {
        List<SysMenu> list = menuService.listAll();
        // 可匿名访问的菜单
        Set<String> anonUrlSet = list.stream()
                .filter(v -> AuthTypeEnum.ANON.equals(v.getAuth()) && StatusEnum.ENABLED.equals(v.getStatus()))
                .map(SysMenu::getCode).collect(Collectors.toSet());
        if (anonUrlSet.contains(menuCode)) {
            return true;
        }
        if (!StpUtil.isLogin()) {
            throw new UnauthorizedException();
        }
        // 登录后可访问的菜单
        Set<String> authUrlSet = list.stream()
                .filter(v -> AuthTypeEnum.AUTH.equals(v.getAuth()) && StatusEnum.ENABLED.equals(v.getStatus()))
                .map(SysMenu::getCode).collect(Collectors.toSet());
        if (authUrlSet.contains(menuCode)) {
            return true;
        }
        // 菜单
        List<Map<String, Object>> menus = (List<Map<String, Object>>) StpUtil.getSession().get("menus");
        Set<String> menuSet = menus.stream().map(v -> v.get("code").toString()).collect(Collectors.toSet());
        if (!menuSet.isEmpty() && menuSet.contains(menuCode)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkProcessPermission(String procCode, List<SysProcess> list) {
        // 可匿名访问的接口
        Set<String> anonProcSet = list.stream()
                .filter(v -> AuthTypeEnum.ANON.equals(v.getAuth()) && StatusEnum.ENABLED.equals(v.getStatus()))
                .map(SysProcess::getProcCode).collect(Collectors.toSet());
        if (anonProcSet.contains(procCode)) {
            return true;
        }
        // 登录后可访问的接口
        Set<String> authProcSet = list.stream()
                .filter(v -> AuthTypeEnum.AUTH.equals(v.getAuth()) && StatusEnum.ENABLED.equals(v.getStatus()))
                .map(SysProcess::getProcCode).collect(Collectors.toSet());
        if (authProcSet.contains(procCode)) {
            return true;
        }
        try {
            // 流程
            List<Map<String, Object>> process = (List<Map<String, Object>>) StpUtil.getSession().get("process");
            // 需鉴权的接口
            Set<String> procSet = process.stream().map(v -> v.get(ProcConstants.PROC_CODE).toString()).collect(Collectors.toSet());
            if (!procSet.isEmpty() && procSet.contains(procCode)) {
                return true;
            }
        } catch (Exception e) {
            // 登录失效
            throw new UnauthorizedException();
        }
        return false;
    }
}
