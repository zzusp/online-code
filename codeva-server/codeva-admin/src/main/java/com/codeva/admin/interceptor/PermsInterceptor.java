package com.codeva.admin.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.codeva.admin.constant.ProcConstants;
import com.codeva.admin.enums.AuthTypeEnum;
import com.codeva.admin.enums.StatusEnum;
import com.codeva.admin.sys.model.SysMenu;
import com.codeva.admin.sys.service.MenuService;
import com.codeva.admin.web.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermsInterceptor implements HandlerInterceptor {

    private final MenuService menuService;

    public PermsInterceptor(MenuService menuService) {
        this.menuService = menuService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String url = request.getServletPath();
        List<SysMenu> list = menuService.listAllWithoutSchema();
        list = list.stream().filter(v -> StringUtils.isNotBlank(v.getUrl())).collect(Collectors.toList());
        // 可匿名访问的接口
        Set<String> anonUrlSet = new HashSet<>();
        // 登录后可访问的接口
        Set<String> authUrlSet = new HashSet<>();
        for (SysMenu v : list) {
            if (StatusEnum.ENABLED.equals(v.getStatus())) {
                if (AuthTypeEnum.ANON.equals(v.getAuth())) {
                    anonUrlSet.add(v.getUrl());
                } else if (AuthTypeEnum.AUTH.equals(v.getAuth())) {
                    authUrlSet.add(v.getUrl());
                }
            }
        }
        if (anonUrlSet.contains(url)) {
            return true;
        }
        // API接口自己做权限校验
        if (url.startsWith(ProcConstants.API_URL)) {
            return true;
        }
        if (StpUtil.isLogin()) {
            if (authUrlSet.contains(url)) {
                return true;
            }
            // 用户接口权限
            // 查询角色权限
            List<Map<String, Object>> roles = (List<Map<String, Object>>) StpUtil.getSession().get("roles");
            // 菜单
            List<Map<String, Object>> menus = (List<Map<String, Object>>) StpUtil.getSession().get("menus");
            // 流程
            List<Map<String, Object>> process = (List<Map<String, Object>>) StpUtil.getSession().get("process");

            // 判断菜单路径
            Set<String> urlSet = menus.stream().filter(v -> v.get("url") != null)
                    .map(v -> v.get("url").toString()).collect(Collectors.toSet());
            if (!urlSet.contains(url)) {
                renderString(response, JSONObject.toJSONString(R.forbidden()));
                return false;
            }
            return true;
        }
        renderString(response, JSONObject.toJSONString(R.unauthorized()));
        return false;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
