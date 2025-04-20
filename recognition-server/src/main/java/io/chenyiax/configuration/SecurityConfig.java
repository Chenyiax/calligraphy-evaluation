package io.chenyiax.configuration;

import io.chenyiax.entity.RestBean;
import io.chenyiax.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 此配置类用于为应用程序启用并配置 Spring Security。
 * 它定义了密码编码、安全过滤器链和 CORS 配置的 bean。
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * JWT 身份验证过滤器，用于处理 JWT 令牌的验证和身份验证。
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 创建一个强度因子为 10 的 BCrypt 密码编码器 bean。
     * 强度因子决定了哈希密码的计算成本，这会影响密码哈希的安全性和性能。
     *
     * @return 一个 BCryptPasswordEncoder 实例。
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * 配置应用程序的安全过滤器链。
     * 此方法设置了各种安全功能，如 CSRF 保护、CORS、请求授权、异常处理、会话管理和 JWT 身份验证。
     *
     * @param http 用于配置安全设置的 HttpSecurity 对象。
     * @return 一个 SecurityFilterChain 实例，表示配置好的安全过滤器链。
     * @throws Exception 如果在配置过程中发生错误。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                // 禁用 CSRF 保护，因为应用程序是无状态的且使用 JWT
                .csrf(AbstractHttpConfigurer::disable)
                // 使用提供的 corsConfigurationSource bean 配置 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 配置请求授权规则
                .authorizeHttpRequests(conf -> {
                    // 允许所有对指定端点的请求
                    conf.requestMatchers("/api/auth/*").permitAll()
                            // 要求所有其他请求进行身份验证
                            .anyRequest().authenticated();
                })
                // 配置异常处理
                .exceptionHandling(conf -> {
                    // 设置访问被拒绝异常的处理程序
                    conf.accessDeniedHandler(this::handleProcess);
                    // 设置身份验证异常的处理程序
                    conf.authenticationEntryPoint(this::handleProcess);
                })
                // 配置会话管理
                .sessionManagement(conf -> {
                    // 将会话创建策略设置为无状态
                    conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                // 在 UsernamePasswordAuthenticationFilter 之前添加 JWT 身份验证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 处理身份验证和访问被拒绝异常。
     * 此方法将包含错误信息的 JSON 响应写入客户端。
     *
     * @param request                   HTTP 请求对象。
     * @param response                  HTTP 响应对象。
     * @param exceptionOrAuthentication 异常或身份验证对象。
     * @throws IOException 如果在写入响应时发生 I/O 错误。
     */
    private void handleProcess(HttpServletRequest request,
                               HttpServletResponse response,
                               Object exceptionOrAuthentication) throws IOException {
        // 设置响应内容类型为 JSON 并使用 UTF-8 编码
        response.setContentType("application/json;charset=utf-8");
        // 获取写入响应内容的写入器
        PrintWriter writer = response.getWriter();

        // 处理访问被拒绝异常
        if (exceptionOrAuthentication instanceof AccessDeniedException exception) {
            // 写入 403 错误信息的 JSON 字符串
            writer.write(RestBean.failure(403, exception.getMessage()).asJsonString());
        }
        // 处理身份验证异常
        else if (exceptionOrAuthentication instanceof AuthenticationException exception) {
            // 写入 401 错误信息的 JSON 字符串
            writer.write(RestBean.failure(401, exception.getMessage()).asJsonString());
        }
    }

    //    @Bean

    /**
     * 创建一个 CORS 配置源 bean。
     * 此方法配置 CORS 设置以允许来自特定源的请求，支持任何标头和方法，并支持凭证。
     *
     * @return 一个 CorsConfigurationSource 实例，表示 CORS 配置。
     */
    public CorsConfigurationSource corsConfigurationSource() {
        // 创建一个新的 CORS 配置对象
        CorsConfiguration corsConfig = new CorsConfiguration();
        // 允许在 CORS 请求中包含凭证
        corsConfig.setAllowCredentials(true);
        // 添加一个允许的源
        corsConfig.addAllowedOrigin("http://localhost:3000");
        // 允许 CORS 请求中的所有标头
        corsConfig.addAllowedHeader("*");
        // 允许 CORS 请求中的所有 HTTP 方法
        corsConfig.addAllowedMethod("*");

        // 创建一个基于 URL 的 CORS 配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 为所有端点注册 CORS 配置
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}