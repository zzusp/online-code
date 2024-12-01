package com.codeva.admin.sys.controller;

import com.codeva.admin.sys.service.AuthService;
import com.codeva.admin.sys.service.MenuService;
import com.codeva.admin.sys.service.ProcessService;
import com.codeva.admin.util.StringUtils;
import com.codeva.admin.web.R;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙鹏
 * @description 统一接口
 * @date Created in 11:10 2024/11/26
 * @modified By
 */
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private final AuthService authService;
    private final ProcessService processService;
    private final MenuService menuService;

    public ApiController(AuthService authService, ProcessService processService, MenuService menuService) {
        this.authService = authService;
        this.processService = processService;
        this.menuService = menuService;
    }

    @GetMapping("/schema/{menuCode}")
    public R<Object> schema(@PathVariable String menuCode) {
        // 连字符转驼峰
        menuCode = StringUtils.hyphenToCamel(menuCode);
        // 鉴权
        if (!authService.checkMenuPermission(menuCode)) {
            return R.forbidden();
        }
        final String code = menuCode;
        return R.ok(menuService.listAll().stream().filter(v -> code.equals(v.getCode())).findFirst().orElse(null));
    }

    @GetMapping("/get/{procCode}")
    public R<Object> get(@PathVariable String procCode, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> vars = new HashMap<>(8);
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            vars.put(entry.getKey(), entry.getValue()[0]);
            if (entry.getValue().length > 1) {
                return R.error("Multi-value is not supported");
            }
        }
        return R.ok(processService.run(StringUtils.hyphenToCamel(procCode), vars));
    }

    @PostMapping("/post/{procCode}")
    public R<Object> post(@PathVariable String procCode, @RequestBody Map<String, Object> vars) {
        return R.ok(processService.run(StringUtils.hyphenToCamel(procCode), vars));
    }

    @DeleteMapping("/delete/{procCode}")
    public R<Object> delete(@PathVariable String procCode, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> vars = new HashMap<>(8);
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            vars.put(entry.getKey(), entry.getValue()[0]);
            if (entry.getValue().length > 1) {
                return R.error("Multi-value is not supported");
            }
        }
        return R.ok(processService.run(StringUtils.hyphenToCamel(procCode), vars));
    }

}
