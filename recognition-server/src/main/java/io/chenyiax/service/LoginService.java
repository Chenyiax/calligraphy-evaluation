package io.chenyiax.service;

import io.chenyiax.entity.User;
import io.chenyiax.entity.WeChatSessionResponse;
import io.chenyiax.entity.WeChatUserDetails;
import io.chenyiax.mapper.UserMapper;
import io.chenyiax.utils.JwtUtils;
import io.chenyiax.utils.WeChatApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    WeChatApiClient weChatApiClient;
    @Autowired
    UserMapper userMapper;

    public String login(String code) {
        WeChatSessionResponse weChatSessionResponse = weChatApiClient.getSessionByCode(code);
        User user = new User();
        user.setOpenid(weChatSessionResponse.getOpenid());
        user.setSessionKey(weChatSessionResponse.getSession_key());
        Integer userId = userMapper.getUserIdByOpenid(user);
        if (userId == null) {
            userMapper.insertUser(user);
            User dataBaseUser = userMapper.getUserById(user.getId());
            WeChatUserDetails weChatUserDetails = new WeChatUserDetails(dataBaseUser);
            return JwtUtils.createJwt(weChatUserDetails);
        } else {
            User dataBaseUser = userMapper.getUserById(userId);
            WeChatUserDetails weChatUserDetails = new WeChatUserDetails(dataBaseUser);
            return JwtUtils.createJwt(weChatUserDetails);
        }
    }
}
