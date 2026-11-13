package org.example.base_project.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.base_project.entity.User;
import org.example.base_project.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class TokenUtils {
    @Resource
    private UserService userService;

    private static UserService staticUserService;

    @PostConstruct
    public void init() {
        staticUserService = userService;
    }

    public static String createToken(String userId, String sign) {
        return JWT.create()
                .withAudience(userId)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1))
                .sign(Algorithm.HMAC256(sign));
    }

    public static String createRefreshToken(String userId, String sign) {
        return JWT.create()
                .withAudience(userId)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 7))
                .sign(Algorithm.HMAC256(sign));
    }

    public static String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("token");
        }
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        return token;
    }

    public static User getCurrentUser() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String token = getTokenFromRequest(request);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        String userIdText = JWT.decode(token).getAudience().get(0);
        Integer userId = Integer.valueOf(userIdText);
        return staticUserService.selectById(userId);
    }
}
