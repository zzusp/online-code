package com.onlinecode.admin.sys.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.web.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    private final ProcessService processService;

    public LoginController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/login")
    public R<Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("username", username);
        vars.put("password", password);
        return R.ok(processService.run("login", vars));
    }

    @GetMapping("/logout")
    public R<Object> logout() {
        StpUtil.logout();
        return R.ok();
    }

    @GetMapping("/isLogin")
    public R<Object> isLogin() {
        return R.ok(StpUtil.isLogin());
    }

}
