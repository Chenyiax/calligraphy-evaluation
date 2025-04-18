package io.chenyiax.utils;

import io.chenyiax.configuration.WeChatConfig;
import io.chenyiax.entity.WeChatSessionResponse;
import io.chenyiax.exception.WeChatApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class WeChatApiClient {
    @Autowired
    WeChatConfig weChatConfig;

    private final RestTemplate restTemplate;

    // 通过构造函数注入 RestTemplate
    public WeChatApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 通过 code 获取微信会话信息
     */
    public WeChatSessionResponse getSessionByCode(String code) {
        String url = buildSessionUrl(code);
        try {
            // 发送 GET 请求并解析响应
            WeChatSessionResponse response = restTemplate.getForObject(url, WeChatSessionResponse.class);
            if (response == null) {
                throw new WeChatApiException(-1, "WeChat interface returns empty response");
            }
            // 检查微信错误码
            if (response.getErrcode() != null && response.getErrcode() != 0) {
                throw new WeChatApiException(response.getErrcode(), response.getErrmsg());
            }
            return response;
        } catch (HttpClientErrorException e) {
            throw new WeChatApiException(-1, "HTTP request fail: " + e.getMessage());
        }
    }

    /**
     * 构建微信接口 URL
     */
    private String buildSessionUrl(String code) {
        return String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weChatConfig.getAppid(), weChatConfig.getSecret(), code
        );
    }
}
