package io.chenyiax.filter;

import io.chenyiax.exception.JwtException;
import io.chenyiax.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 该组件类用于在 Spring Security 过滤器链中进行 JWT 身份验证。
 * 继承自 OncePerRequestFilter 确保每个请求只过滤一次。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * 注入 JwtUtils 工具类，用于解析 JWT 令牌。
     */
    private final JwtUtils jwtUtils;

    /**
     * 重写 doFilterInternal 方法，在该方法中实现 JWT 身份验证逻辑。
     * 该方法会在每个请求经过此过滤器时被调用。
     *
     * @param request  HTTP 请求对象，包含客户端的请求信息。
     * @param response HTTP 响应对象，用于向客户端发送响应信息。
     * @param filterChain 过滤器链对象，用于将请求传递给下一个过滤器。
     * @throws ServletException 处理 Servlet 相关操作时可能抛出的异常。
     * @throws IOException 处理输入输出操作时可能抛出的异常。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取名为 "Authorization" 的信息
        String authorization = request.getHeader("Authorization");

        // 检查 "Authorization" 信息是否存在且以 "Bearer " 开头
        if (authorization != null && authorization.startsWith("Bearer ")) {
            // 截取 "Bearer " 之后的部分作为 JWT 令牌
            String token = authorization.substring(7);
            // 调用 JwtUtils 工具类的 parseToken 方法将令牌解析为 UserDetails 对象
            // 如果解析结果为 null，说明解析失败，JWT 令牌无效
            UserDetails user = jwtUtils.parseToken(token);
            // 打印解析得到的用户信息
            System.out.println(user);
            // 检查解析得到的用户信息是否不为 null
            if (user != null) {
                // 使用 UsernamePasswordAuthenticationToken 作为身份验证实体，并填充相关用户信息
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                // 设置身份验证的详细信息，包含请求的相关信息
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将配置好的 Authentication 对象设置到 SecurityContext 中，表示身份验证已完成
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 若解析失败，抛出 JwtException 异常并给出错误信息
                throw new JwtException("jwt parse error");
            }
        }
        // 最后，将请求和响应传递给过滤器链中的下一个过滤器
        filterChain.doFilter(request, response);
    }
}