package org.example.base_project.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 请求日志工具类。
 *
 * 说明：
 * 1. 只用于轻量级日志记录，不连接数据库。
 * 2. 不记录密码、token 等敏感信息。
 * 3. 兼容 nginx / 网关代理后的真实 IP 获取。
 */
public class RequestLogUtils {

    private RequestLogUtils() {
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return isBlankOrUnknown(ip) ? "unknown" : ip;
    }

    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isBlank()) {
            return "unknown";
        }
        return userAgent.length() > 120 ? userAgent.substring(0, 120) : userAgent;
    }

    private static boolean isBlankOrUnknown(String value) {
        return value == null || value.isBlank() || "unknown".equalsIgnoreCase(value);
    }
}
