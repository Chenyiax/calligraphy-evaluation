package io.chenyiax.entity;

import lombok.Data;

@Data
public class WeChatSessionResponse {
    private String openid;
    private String session_key;
    private Integer errcode; // 微信错误码（0 表示成功）
    private String errmsg;
}
