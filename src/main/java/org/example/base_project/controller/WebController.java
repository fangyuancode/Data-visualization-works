package org.example.base_project.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.base_project.common.Result;
import org.example.base_project.entity.Account;
import org.example.base_project.entity.LoginUserResult;
import org.example.base_project.entity.User;
import org.example.base_project.service.UserService;
import org.example.base_project.utils.RequestLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

// 添加注解，RestController 对外提供数据接口的类，标注该类是一个接口，返回 json 数据。
@RestController
@RequestMapping("/api")
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @Resource
    private UserService userService;

    // 旧版登录接口，保留兼容。
    @PostMapping("/login")
    public Result login(@RequestBody Account account, HttpServletRequest request) {
        String username = account == null ? null : account.getUsername();
        log.info("用户登录请求[兼容接口]：username={}, ip={}, userAgent={}",
                username,
                RequestLogUtils.getClientIp(request),
                RequestLogUtils.getUserAgent(request));

        try {
            LoginUserResult dbaccount = userService.login(account);
            log.info("用户登录成功[兼容接口]：username={}, ip={}", username, RequestLogUtils.getClientIp(request));
            return Result.success(dbaccount);
        } catch (RuntimeException e) {
            log.warn("用户登录失败[兼容接口]：username={}, ip={}, reason={}",
                    username,
                    RequestLogUtils.getClientIp(request),
                    e.getMessage());
            throw e;
        }
    }

    // 旧版注册接口，保留兼容。
    @PostMapping("/register")
    public Result register(@RequestBody User user, HttpServletRequest request) {
        String username = user == null ? null : user.getUsername();
        log.info("用户注册请求[兼容接口]：username={}, ip={}", username, RequestLogUtils.getClientIp(request));

        try {
            userService.register(user);
            log.info("用户注册成功[兼容接口]：username={}, ip={}", username, RequestLogUtils.getClientIp(request));
            return Result.success("注册成功！");
        } catch (RuntimeException e) {
            log.warn("用户注册失败[兼容接口]：username={}, ip={}, reason={}",
                    username,
                    RequestLogUtils.getClientIp(request),
                    e.getMessage());
            throw e;
        }
    }
}
