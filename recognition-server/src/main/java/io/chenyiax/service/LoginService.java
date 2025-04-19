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
 * This service class is responsible for handling user login operations,
 * especially for WeChat login. It interacts with the WeChat API,
 * manages user information in the database, and generates JWT tokens.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {
    private final WeChatApiClient weChatApiClient;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    /**
     * Handles the user login process using the WeChat authorization code.
     *
     * @param code The authorization code provided by WeChat during the login process.
     * @return A JWT token representing the user's login session.
     * @throws UserCreationException If the user creation fails.
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
     * Creates a JWT token for the given user.
     *
     * @param user The user object for which the token is to be created.
     * @return A JWT token representing the user's login session.
     */
    private String createToken(User user) {
        WeChatUserDetails weChatUserDetails = new WeChatUserDetails(user);
        return jwtUtils.createToken(weChatUserDetails);
    }
}
