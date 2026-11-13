package org.example.base_project.utils;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.example.base_project.exception.CustomException;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

/**
 * IP地址工具类
 * 提供获取客户端真实IP地址的方法
 */
public class IpUtils {

    // 类路径下的相对路径
    private static final String XDB_RESOURCE_PATH = "IP/csdn-ip2region.xdb";

    // 缓存Searcher实例（线程安全）
    private static Searcher cachedSearcher;

    // 静态初始化块：在类加载时初始化Searcher
    static {
        try {
            // 1. 从类路径加载资源
            ClassPathResource resource = new ClassPathResource(XDB_RESOURCE_PATH);

            // 2. 检查资源是否存在
            if (!resource.exists()) {
                throw new RuntimeException("IP数据库文件未找到: " + XDB_RESOURCE_PATH);
            }

            // 3. 创建临时文件（因为ip2region需要文件路径）
            File tempFile = File.createTempFile("ip2region_", ".xdb");
            tempFile.deleteOnExit(); // 程序退出时删除临时文件

            // 4. 将资源流复制到临时文件
            try (InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // 5. 使用临时文件路径创建Searcher
            cachedSearcher = Searcher.newWithFileOnly(tempFile.getAbsolutePath());

            System.out.println("IP数据库加载成功: " + tempFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("初始化IP数据库失败", e);
        }
    }

    /**
     * 获取当前请求的客户端IP地址
     * 通过Spring的RequestContextHolder获取当前请求对象
     *
     * @return 客户端真实IP地址
     */
    public static String getClientIpAddress() {
        // 通过Spring的RequestContextHolder获取当前线程绑定的请求属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // 从请求属性中获取HttpServletRequest对象
        HttpServletRequest request = attributes.getRequest();
        // 调用重载方法处理具体IP获取逻辑
        return getClientIpAddress(request);
    }

    /**
     * 从HttpServletRequest对象中解析客户端真实IP地址
     * 按照优先级检查多个代理相关的Header（防止经过代理服务器时IP被隐藏）
     *
     * @param request HttpServletRequest对象
     * @return 解析后的客户端真实IP
     */
    private static String getClientIpAddress(HttpServletRequest request) {
        String ip;
        // 1. 优先检查X-Forwarded-For（标准代理转发Header）
        ip = request.getHeader("X-Forwarded-For");
        if (isInvalidIp(ip)) {
            // 2. 检查Proxy-Client-IP（Apache代理专用Header）
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            // 3. 检查WL-Proxy-Client-IP（WebLogic代理专用Header）
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            // 4. 检查HTTP_CLIENT_IP（非标准代理Header）
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isInvalidIp(ip)) {
            // 5. 检查HTTP_X_FORWARDED_FOR（非标准代理Header）
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isInvalidIp(ip)) {
            // 6. 所有Header都无效时，使用TCP连接的远程地址
            ip = request.getRemoteAddr();
        }

        // 处理多级代理场景：取第一个非unknown的IP（逗号分隔的IP列表）
        if (ip != null && ip.contains(",")) {
            // 分割IP列表并取第一个有效IP
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 验证IP地址是否有效
     *
     * @param ip 待验证的IP字符串
     * @return 无效返回true（null/空/unknown）
     */
    private static boolean isInvalidIp(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取IP归属地信息
     * @param ip 要查询的IP地址
     * @return IP归属地信息，查询失败返回"未知"
     */
    public static String getIpPossessionByFile(String ip) {
        if (StringUtils.isNotEmpty(ip)) {
            try {
                // 使用缓存的Searcher实例
                long sTime = System.nanoTime();

                // 执行查询
                String region = cachedSearcher.search(ip);

                // 计算耗时
                long cost = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - sTime);
                System.out.printf("IP查询完成: %s → %s (耗时: %d μs)%n", ip, region, cost);

                // 清洗数据：移除未知字段(|0)
                region = region.replace("|0", "");

                return region;
            } catch (Exception e) {
                // 记录详细错误信息
                System.err.println("IP查询失败: " + ip);
                e.printStackTrace();

                throw new CustomException("500", "获取IP地址异常: " + e.getMessage());
            }
        }
        return "未知";
    }
}