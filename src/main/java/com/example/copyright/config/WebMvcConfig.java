package com.example.copyright.config;

import com.example.copyright.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有 API 请求
                .excludePathPatterns(
                        "/api/users/login",        // 登录接口
                        "/api/users/register",     // 注册接口
                        "/api/registration/submit", // 提交注册申请接口（允许未登录访问）
                        "/api/registration/my-status", // 查询我的申请状态（允许未登录访问）
                        "/api/admin/**",           // 管理员接口（用于初始化权限）
                        "/api/blockchain/**",      // 区块链浏览器接口（公开访问）
                        "/api/explorer/**",        // 区块链浏览器接口（公开访问）
                        "/swagger-ui/**",          // Swagger UI
                        "/swagger-resources/**",   // Swagger 资源
                        "/v2/api-docs",            // Swagger API 文档
                        "/webjars/**",             // Swagger 依赖
                        "/error"                   // 错误页面
                );
    }
}
