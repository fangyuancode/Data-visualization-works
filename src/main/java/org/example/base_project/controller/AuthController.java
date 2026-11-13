package org.example.base_project.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.base_project.common.Result;
import org.example.base_project.entity.Account;
import org.example.base_project.entity.LoginResponse;
import org.example.base_project.entity.User;
import org.example.base_project.service.UserService;
import org.example.base_project.utils.RequestLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody Account account, HttpServletRequest request) {
        String username = account == null ? null : account.getUsername();
        log.info("用户登录请求：username={}, ip={}, userAgent={}",
                username,
                RequestLogUtils.getClientIp(request),
                RequestLogUtils.getUserAgent(request));

        try {
            LoginResponse response = userService.loginForFrontend(account);
            log.info("用户登录成功：username={}, ip={}", username, RequestLogUtils.getClientIp(request));
            return Result.success(response);
        } catch (RuntimeException e) {
            log.warn("用户登录失败：username={}, ip={}, reason={}",
                    username,
                    RequestLogUtils.getClientIp(request),
                    e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user, HttpServletRequest request) {
        String username = user == null ? null : user.getUsername();
        log.info("用户注册请求：username={}, ip={}", username, RequestLogUtils.getClientIp(request));

        try {
            userService.register(user);
            log.info("用户注册成功：username={}, ip={}", username, RequestLogUtils.getClientIp(request));
            return Result.success("注册成功！");
        } catch (RuntimeException e) {
            log.warn("用户注册失败：username={}, ip={}, reason={}",
                    username,
                    RequestLogUtils.getClientIp(request),
                    e.getMessage());
            throw e;
        }
    }
}
