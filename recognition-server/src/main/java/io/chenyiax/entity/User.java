package io.chenyiax.entity;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private String openid;
    private String sessionKey;

    private int id;
    private String nickname;
    private String avatarUrl;
    private List<String> auth;
}
