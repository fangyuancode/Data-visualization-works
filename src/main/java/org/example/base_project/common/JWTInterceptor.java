package org.example.base_project.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.base_project.entity.User;
import org.example.base_project.exception.CustomException;
import org.example.base_project.service.UserService;
import org.example.base_project.utils.TokenUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class JWTInterceptor implements HandlerInterceptor {
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = TokenUtils.getTokenFromRequest(request);
        if (StrUtil.isBlank(token)) {
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }

        User account;
        try {
            String userIdText = JWT.decode(token).getAudience().get(0);
            Integer userId = Integer.valueOf(userIdText);
            account = userService.selectById(userId);
        } catch (Exception e) {
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }

        if (account == null || StrUtil.isBlank(account.getPassword())) {
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }

        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }
        return true;
    }
}
