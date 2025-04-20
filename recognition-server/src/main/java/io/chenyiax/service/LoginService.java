package io.chenyiax.service;

import io.chenyiax.entity.User;
import io.chenyiax.entity.WeChatSessionResponse;
import io.chenyiax.entity.WeChatUserDetails;
import io.chenyiax.exception.UserCreationException;
import io.chenyiax.exception.WeChatApiException;
import io.chenyiax.mapper.UserMapper;
import io.chenyiax.utils.JwtUtils;
import io.chenyiax.utils.WeChatApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * LoginService 类是一个服务层组件，负责处理微信用户的登录业务逻辑。
 * 它使用 Spring 的 @Service 注解将其注册为一个服务 bean，
 * 并通过 @RequiredArgsConstructor 注解自动生成包含 final 字段的构造函数，
 * 使用 @Transactional 注解保证业务操作的事务性。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {
    private final WeChatApiClient weChatApiClient;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    /**
     * 处理微信用户的登录逻辑。
     * 通过微信提供的临时登录凭证 code 获取用户会话信息，
     * 若用户不存在则创建新用户，最后为用户生成 JWT 令牌。
     *
     * @param code 微信客户端返回的临时登录凭证，用于向微信服务器验证用户身份。
     * @return 生成的 JWT 令牌字符串，用于后续的身份验证。
     * @throws WeChatApiException 当调用微信接口获取会话信息失败时抛出该异常。
     * @throws UserCreationException 当创建新用户失败时抛出该异常。
     */
    public String login(String code) {
        WeChatSessionResponse weChatSessionResponse = weChatApiClient.getSessionByCode(code);
        if (weChatSessionResponse == null) {
            throw new WeChatApiException("Get Wechat session failed");
        }

        User user = new User();
        user.setOpenid(weChatSessionResponse.getOpenid());
        user.setSessionKey(weChatSessionResponse.getSession_key());

        Integer userId = userMapper.getUserIdByOpenid(user);
        if (userId == null) {
            userMapper.insertUser(user);
            // 确保获取到插入后的用户ID
            userId = userMapper.getUserIdByOpenid(user);
            if (userId == null) {
                throw new UserCreationException("User creation failed");
            }
        }

        User dataBaseUser = userMapper.getUserById(userId);
        return createToken(dataBaseUser);
    }

    /**
     * 为指定用户创建 JWT 令牌。
     *
     * @param user 包含用户信息的 User 对象，用于生成 JWT 令牌。
     * @return 生成的 JWT 令牌字符串。
     */
    private String createToken(User user) {
        WeChatUserDetails weChatUserDetails = new WeChatUserDetails(user);
        return jwtUtils.createToken(weChatUserDetails);
    }
}
