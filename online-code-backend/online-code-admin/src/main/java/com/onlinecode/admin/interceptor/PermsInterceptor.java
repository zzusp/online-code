package com.onlinecode.admin.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.onlinecode.admin.constant.ProcConstants;
import com.onlinecode.admin.enums.AuthTypeEnum;
import com.onlinecode.admin.enums.StatusEnum;
import com.onlinecode.admin.process.model.JsonVars;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.proto.RunProto;
import com.onlinecode.admin.web.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermsInterceptor implements HandlerInterceptor {

    private final ProcessService processService;

    public PermsInterceptor(ProcessService processService) {
        this.processService = processService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        List<SysProcess> list = processService.listAll();
        // 可匿名访问的接口
        Set<String> anonProcSet = list.stream()
                .filter(v -> AuthTypeEnum.AUTH.equals(v.getAuth()) && StatusEnum.ENABLED.equals(v.getStatus()))
                .map(SysProcess::getProcCode).collect(Collectors.toSet());
        RepeatedlyRequestWrapper requestWrapper = new RepeatedlyRequestWrapper(request, response);

        byte[] bytes = ("\n\r" + RepeatedlyRequestWrapper.getBodyString(requestWrapper)).getBytes();
        RunProto.Run proto = RunProto.Run.parseFrom(bytes);

        // 登录页面免登录
        if (proto != null && ProcConstants.MENU_GET_BY_CODE.equals(proto.getProcCode())) {
            return true;
        }
        String url = request.getServletPath();
        String procCode = proto != null ? proto.getProcCode() : null;
        if (ProcConstants.PROC_RUN_URL.equals(url) && StringUtils.isNoneBlank(procCode) && anonProcSet.contains(procCode)) {
            return true;
        }
        if (StpUtil.isLogin()) {
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
            if (urlSet.contains(url)) {
                return true;
            }
            // 判断菜单编码
            if (proto != null && ProcConstants.MENU_GET_BY_CODE.equals(procCode)) {
                JsonVars vars = JsonVars.parse(proto.getVars());
                Set<String> menuSet = menus.stream().map(v -> v.get("code").toString()).collect(Collectors.toSet());
                if (!menuSet.isEmpty() && vars != null && menuSet.contains(vars.get("code").toString())) {
                    return true;
                }
            }
            // 判断流程编码
            Set<String> procSet = process.stream().map(v -> v.get(ProcConstants.PROC_CODE).toString()).collect(Collectors.toSet());
            if (ProcConstants.PROC_RUN_URL.equals(url) && !procSet.isEmpty() && procSet.contains(procCode)) {
                return true;
            }
            renderString(response, JSONObject.toJSONString(R.forbidden()));
            return false;
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
