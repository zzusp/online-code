package com.codeva.admin.sys.service;

import com.codeva.admin.sys.model.SysMenu;

import java.util.List;

/**
 * @author 孙鹏
 * @description 菜单服务
 * @date Created in 17:26 2024/11/30
 * @modified By
 */
public interface MenuService {

    /**
     * 查询所有菜单信息
     *
     * @return 所有菜单信息
     */
    List<SysMenu> listAll();

}
