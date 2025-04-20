package io.chenyiax.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.print.DocFlavor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * WeChatUserDetails 类实现了 Spring Security 的 UserDetails 接口，
 * 用于封装微信用户的详细信息，以便在 Spring Security 框架中进行身份验证和授权操作。
 */
@Data
public class WeChatUserDetails implements UserDetails {

    /**
     * 封装的用户对象，包含用户的基本信息、权限等。
     */
    private User user;

    /**
     * 构造函数，用于初始化 WeChatUserDetails 对象。
     *
     * @param user 微信用户对象，包含用户的相关信息。
     */
    public WeChatUserDetails(User user) {
        this.user = user;
    }

    /**
     * 获取用户的权限集合。
     * 从用户对象中获取权限列表，并将其转换为 Spring Security 所需的 GrantedAuthority 集合。
     *
     * @return 包含用户权限的集合。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将用户的权限列表转换为 SimpleGrantedAuthority 集合
        return user.getAuth().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的密码。
     * 这里使用用户的会话密钥作为密码，用于身份验证。
     *
     * @return 用户的会话密钥。
     */
    @Override
    public String getPassword() {
        return user.getSessionKey();
    }

    /**
     * 获取用户的用户名。
     * 这里使用用户的开放 ID 作为用户名，用于身份验证。
     *
     * @return 用户的开放 ID。
     */
    @Override
    public String getUsername() {
        return user.getOpenid();
    }

    /**
     * 判断用户账户是否未过期。
     * 这里默认返回 true，表示用户账户永不过期。
     *
     * @return 如果账户未过期返回 true，否则返回 false。
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 判断用户账户是否未锁定。
     * 这里默认返回 true，表示用户账户永不锁定。
     *
     * @return 如果账户未锁定返回 true，否则返回 false。
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 判断用户的凭证是否未过期。
     * 这里默认返回 true，表示用户的凭证永不过期。
     *
     * @return 如果凭证未过期返回 true，否则返回 false。
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 判断用户是否已启用。
     * 这里默认返回 true，表示用户账户已启用。
     *
     * @return 如果用户已启用返回 true，否则返回 false。
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}