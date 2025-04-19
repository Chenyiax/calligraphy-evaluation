package io.chenyiax.entity;

import lombok.Data;

@Data
public class WeChatSessionResponse {
    private String openid;
    private String session_key;
    private Integer errcode;
    private String errmsg;
}
