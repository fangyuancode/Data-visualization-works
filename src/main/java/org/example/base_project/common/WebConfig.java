package org.example.base_project.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 常规登录拦截：保持原项目 /api/common/** 查询接口不强制登录的设计。
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/login",
                        "/api/register",
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/common/**",
                        "/error"
                );

        // 写操作权限拦截：限制 test / R_USER 新增、编辑、删除。
        registry.addInterceptor(dataWritePermissionInterceptor())
                .addPathPatterns(
                        "/api/common/**",
                        "/api/user/**",
                        "/api/role/**"
                );
    }

    @Bean
    public JWTInterceptor jwtInterceptor() {
        return new JWTInterceptor();
    }

    @Bean
    public DataWritePermissionInterceptor dataWritePermissionInterceptor() {
        return new DataWritePermissionInterceptor();
    }
}
