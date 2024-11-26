package com.codeva.admin.sys.controller;

import com.codeva.admin.constant.ProcConstants;
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

    private final ProcessService processService;

    public ApiController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/schema/{code}")
    public R<Object> schema(@PathVariable String code) {
        Map<String, Object> vars = new HashMap<>(8);
        vars.put("code", StringUtils.hyphenToCamel(code));
        return R.ok(processService.run(ProcConstants.MENU_GET_BY_CODE, vars));
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

}
