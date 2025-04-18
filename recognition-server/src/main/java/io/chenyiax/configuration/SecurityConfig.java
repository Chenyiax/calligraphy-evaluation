package io.chenyiax.configuration;

import io.chenyiax.entity.RestBean;
import io.chenyiax.filter.JwtAuthenticationFilter;

import io.chenyiax.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)  // 禁用 CSRF（对于无状态 API）
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 配置 CORS
                .authorizeHttpRequests(conf -> {
                    // 允许登录接口不需要认证
                    conf.requestMatchers("/api/auth/*", "/error").permitAll()
                            .anyRequest().authenticated();
                })
                .exceptionHandling(conf -> {
                    // 认证和授权失败时处理
                    conf.accessDeniedHandler(this::handleProcess);
                    conf.authenticationEntryPoint(this::handleProcess);
                })
                .sessionManagement(conf -> {
                    // 设置会话策略为无状态
                    conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // 添加 JWT 认证过滤器
                .build();
    }


    private void handleProcess(HttpServletRequest request,
                               HttpServletResponse response,
                               Object exceptionOrAuthentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();

        if (exceptionOrAuthentication instanceof AccessDeniedException exception) {
            writer.write(RestBean.failure(403, exception.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof AuthenticationException exception) {
            writer.write(RestBean.failure(401, exception.getMessage()).asJsonString());
        }
    }

    //    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);  // 允许发送凭证（cookies）
        corsConfig.addAllowedOrigin("http://localhost:3000");  // 允许来自 localhost:3000 的请求
        corsConfig.addAllowedHeader("*");  // 允许所有请求头
        corsConfig.addAllowedMethod("*");  // 允许所有 HTTP 方法（GET, POST, PUT, DELETE 等）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // 配置所有接口允许跨域
        return source;
    }
}

