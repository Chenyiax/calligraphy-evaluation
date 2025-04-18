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

@Data
public class WeChatUserDetails implements UserDetails {
    private User user;

    public WeChatUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 根据业务需求返回权限，这里返回空集合
        return user.getAuth().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        // 微信登录没有传统密码，可以用sessionKey代替
        return user.getSessionKey();
    }

    @Override
    public String getUsername() {
        // 使用openid作为用户名
        return user.getOpenid();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 账户是否未过期 - 微信用户通常不过期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 账户是否未锁定 - 微信用户通常不锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 凭证是否未过期 - 微信session_key有过期时间，这里简化处理
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 账户是否启用 - 微信用户通常启用
        return true;
    }
}