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
 * This class represents the user details for WeChat users, implementing the Spring Security's UserDetails interface.
 * It encapsulates the user information and provides methods to retrieve user - related security information,
 * such as authorities, password, username, and account status.
 *
 * @author Your Name (Replace with actual author)
 */
@Data
public class WeChatUserDetails implements UserDetails {
    /**
     * An instance of the User class that stores the actual user information.
     * It contains fields like openid, sessionKey, and user roles.
     */
    private User user;

    /**
     * Constructs a new WeChatUserDetails object with the given User instance.
     *
     * @param user The User object containing the WeChat user's information.
     */
    public WeChatUserDetails(User user) {
        this.user = user;
    }

    /**
     * Retrieves the collection of authorities granted to the user.
     * It maps each role in the user's role list to a SimpleGrantedAuthority object,
     * prefixing each role with "ROLE_".
     *
     * @return A collection of GrantedAuthority objects representing the user's authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert the user's role list to a collection of GrantedAuthority objects
        return user.getAuth().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the password used for authentication.
     * Since WeChat login does not use a traditional password,
     * the session key is used as a substitute.
     *
     * @return The session key of the WeChat user.
     */
    @Override
    public String getPassword() {
        // Use the session key as the password for authentication
        return user.getSessionKey();
    }

    /**
     * Retrieves the username used for authentication.
     * The WeChat user's openid is used as the username.
     *
     * @return The openid of the WeChat user.
     */
    @Override
    public String getUsername() {
        // Use the openid as the username for authentication
        return user.getOpenid();
    }

    /**
     * Checks if the user's account has not expired.
     * Since WeChat users typically do not have an account expiration mechanism,
     * this method always returns true.
     *
     * @return true if the account has not expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        // WeChat users usually do not have an account expiration
        return true;
    }

    /**
     * Checks if the user's account is not locked.
     * Since WeChat users are typically not locked,
     * this method always returns true.
     *
     * @return true if the account is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        // WeChat users are usually not locked
        return true;
    }

    /**
     * Checks if the user's credentials have not expired.
     * Although the WeChat session key has an expiration time,
     * this method simplifies the process and always returns true.
     *
     * @return true if the credentials have not expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // Simplify the session key expiration check and always return true
        return true;
    }

    /**
     * Checks if the user's account is enabled.
     * Since WeChat users are typically enabled,
     * this method always returns true.
     *
     * @return true if the account is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        // WeChat users are usually enabled
        return true;
    }
}