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
import org.example.base_project.utils.RequestLogUtils;
import org.example.base_project.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * 数据写操作权限拦截器。
 *
 * 作用：
 * 1. /api/common/** 原项目为了方便查询被排除在 JWTInterceptor 外，GET 查询继续保持放行。
 * 2. 对 POST、PUT、PATCH、DELETE 写操作进行登录校验和角色校验。
 * 3. test 账号和 R_USER 普通用户只能查看，不能新增、编辑、删除。
 * 4. 记录轻量级权限日志，便于部署后排查“谁被拒绝 / 谁执行了写操作”。
 */
public class DataWritePermissionInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DataWritePermissionInterceptor.class);

    @Resource
    private UserService userService;

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String method = request.getMethod().toUpperCase();

        // GET 等查询请求只读放行，保持原有页面查看能力。
        if (!WRITE_METHODS.contains(method)) {
            return true;
        }

        User currentUser = getAndVerifyUser(request);

        if (!userService.hasWritePermission(currentUser)) {
            log.warn("写操作被拒绝：userId={}, username={}, method={}, uri={}, ip={}",
                    currentUser.getId(),
                    currentUser.getUsername(),
                    request.getMethod(),
                    request.getRequestURI(),
                    RequestLogUtils.getClientIp(request));

            throw new CustomException("403", "当前账号只有查看权限，不能新增、编辑或删除数据");
        }

        log.info("写操作放行：userId={}, username={}, method={}, uri={}, ip={}",
                currentUser.getId(),
                currentUser.getUsername(),
                request.getMethod(),
                request.getRequestURI(),
                RequestLogUtils.getClientIp(request));

        return true;
    }

    private User getAndVerifyUser(HttpServletRequest request) {
        String token = TokenUtils.getTokenFromRequest(request);

        if (StrUtil.isBlank(token)) {
            log.warn("写操作未携带 token：method={}, uri={}, ip={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    RequestLogUtils.getClientIp(request));
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }

        User account;
        try {
            String userIdText = JWT.decode(token).getAudience().get(0);
            Integer userId = Integer.valueOf(userIdText);
            account = userService.selectById(userId);
        } catch (Exception e) {
            log.warn("token 解析失败：method={}, uri={}, ip={}, reason={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    RequestLogUtils.getClientIp(request),
                    e.getMessage());
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }

        if (account == null || StrUtil.isBlank(account.getPassword())) {
            log.warn("token 对应用户不存在：method={}, uri={}, ip={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    RequestLogUtils.getClientIp(request));
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }

        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            log.warn("token 验证失败：userId={}, username={}, method={}, uri={}, ip={}",
                    account.getId(),
                    account.getUsername(),
                    request.getMethod(),
                    request.getRequestURI(),
                    RequestLogUtils.getClientIp(request));
            throw new CustomException("401", "登录状态已失效，请重新登录");
        }

        return account;
    }
}
