package com.onlinecode.admin.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.onlinecode.admin.process.model.RunParam;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.web.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermsInterceptor implements HandlerInterceptor {

    private final ProcessService processService;

    public PermsInterceptor(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        List<SysProcess> list = processService.listAll();
        if (list.isEmpty()) {
            renderString(response, JSONObject.toJSONString(R.unauthorized()));
            return false;
        }
        // 可匿名访问的接口
        Set<String> anonProcSet = list.stream().filter(v -> "0".equals(v.getAuth()) && "1".equals(v.getStatus()))
                .map(SysProcess::getProcCode).collect(Collectors.toSet());
        if (anonProcSet.isEmpty()) {
            renderString(response, JSONObject.toJSONString(R.unauthorized()));
            return false;
        }
        RepeatedlyRequestWrapper requestWrapper = new RepeatedlyRequestWrapper(request, response);
        RunParam param = JSONObject.parseObject(RepeatedlyRequestWrapper.getBodyString(requestWrapper), RunParam.class);
        // 登录页面免登录
        if (param != null && "menuGetByCode".equals(param.getProcCode()) && "login".equals(param.getVars().get("code"))) {
            return true;
        }
        String procCode = param != null ? param.getProcCode() : null;
        if (StringUtils.isNoneBlank(procCode) && anonProcSet.contains(procCode)) {
            return true;
        }
        if (StpUtil.isLogin()) {
            String url = request.getServletPath();
            Set<String> authUrlSet = new HashSet<>();
            authUrlSet.add("/process/list");
            authUrlSet.add("/process/getById");
            authUrlSet.add("/process/getInfoWithTaskById");
            authUrlSet.add("/process/save");
            authUrlSet.add("/process/copy");
            authUrlSet.add("/process/delete");
            authUrlSet.add("/process/run");
            authUrlSet.add("/process/runTask");
            authUrlSet.add("/process/runCmd");
            if (authUrlSet.contains(url)) {
                return true;
            }
            Set<String> authProcSet = list.stream().filter(v -> "1".equals(v.getAuth()) && "1".equals(v.getStatus()))
                    .map(SysProcess::getProcCode).collect(Collectors.toSet());
            Set<String> permsProcSet = list.stream().filter(v -> "2".equals(v.getAuth()) && "1".equals(v.getStatus()))
                    .map(SysProcess::getProcCode).collect(Collectors.toSet());
            if (!authProcSet.isEmpty() && authProcSet.contains(procCode)) {
                return true;
            }
            // TODO 用户绑定接口权限
            if (!permsProcSet.isEmpty() && permsProcSet.contains(procCode)) {
                return true;
            }
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
