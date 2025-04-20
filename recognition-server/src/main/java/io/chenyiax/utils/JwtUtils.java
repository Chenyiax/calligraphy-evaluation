package io.chenyiax.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.chenyiax.configuration.JwtConfig;
import io.chenyiax.exception.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * JwtUtils 是一个工具类，用于处理 JWT（JSON Web Token）的创建和解析操作。
 * 该类被 Spring 管理，使用 Lombok 的 @RequiredArgsConstructor 注解自动生成构造函数，
 * 依赖于 JwtConfig 来获取 JWT 相关配置，依赖于 Clock 来获取当前时间。
 */
@Component
@RequiredArgsConstructor
public class JwtUtils {
    /**
     * JWT 配置对象，用于获取 JWT 的密钥和有效期等配置信息。
     */
    private final JwtConfig jwtConfig;
    /**
     * 时钟对象，用于获取当前时间，确保时间的准确性和可测试性。
     */
    private final Clock clock;

    /**
     * 根据用户信息创建一个 JWT 令牌。
     *
     * @param user 包含用户信息的 UserDetails 对象，用于填充 JWT 中的声明信息。
     * @return 生成的 JWT 令牌字符串。
     */
    public String createToken(UserDetails user) {
        // 使用 HMAC256 算法并结合配置中的密钥创建一个加密算法实例
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getKey());
        // 获取当前时间
        Instant now = Instant.now(clock);
        // 计算令牌的过期时间，当前时间加上配置中的有效期（单位：秒）
        Instant expiresAt = now.plus(jwtConfig.getValidity(), ChronoUnit.SECONDS);

        // 构建 JWT 令牌，设置声明信息并签名
        return JWT.create()
                // 设置用户姓名声明
                .withClaim("name", user.getUsername())
                // 设置用户权限声明，将用户的权限集合转换为字符串列表
                .withClaim("authorities", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                // 设置令牌的过期时间
                .withExpiresAt(Date.from(expiresAt))
                // 设置令牌的签发时间
                .withIssuedAt(Date.from(now))
                // 使用指定算法对令牌进行签名
                .sign(algorithm);
    }

    /**
     * 解析 JWT 令牌并返回用户信息。
     *
     * @param token 待解析的 JWT 令牌字符串。
     * @return 包含用户信息的 UserDetails 对象。
     * @throws JwtException 如果令牌验证失败或已过期，抛出该异常。
     */
    public UserDetails parseToken(String token) throws JwtException {
        try {
            // 使用 HMAC256 算法并结合配置中的密钥创建一个加密算法实例
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getKey());
            // 创建一个 JWT 验证器并验证令牌
            DecodedJWT jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);
            // 获取 JWT 中的声明信息
            Map<String, Claim> claims = jwt.getClaims();
            // 检查令牌是否已过期
            if (Instant.now(clock).isAfter(claims.get("exp").asDate().toInstant())) {
                // 若已过期，抛出 JwtException 异常
                throw new JwtException("Token expired");
            }

            // 根据声明信息构建 UserDetails 对象
            return User.withUsername(claims.get("name").asString())
                    // 密码置空，因为 JWT 验证不依赖密码
                    .password("")
                    // 设置用户权限
                    .authorities(claims.get("authorities").asArray(String.class))
                    .build();
        } catch (JWTVerificationException e) {
            // 若令牌验证失败，抛出 JwtException 异常并附带错误信息
            throw new JwtException("Invalid token" + e);
        }
    }
}