package io.chenyiax.entity;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private String openid;
    private String sessionKey;

    // 更多微信用户信息字段
    private int id;
    private String nickname;
    private String avatarUrl;
    private List<String> auth;
}
