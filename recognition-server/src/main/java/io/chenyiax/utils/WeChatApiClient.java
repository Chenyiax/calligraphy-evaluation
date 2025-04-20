package io.chenyiax.utils;

import io.chenyiax.configuration.WeChatConfig;
import io.chenyiax.entity.WeChatSessionResponse;
import io.chenyiax.exception.WeChatApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * WeChatApiClient 是一个用于调用微信接口的客户端类。
 * 该类借助 Spring 的依赖注入机制，注入微信配置信息和 RestTemplate 来完成与微信接口的交互。
 * 它提供了通过微信临时登录凭证 code 获取会话信息的功能。
 */
@Component
@RequiredArgsConstructor
public class WeChatApiClient {

    /**
     * 注入微信配置信息，包含 appid、secret 等关键信息，用于构造微信接口请求的 URL。
     */
    private final WeChatConfig weChatConfig;

    /**
     * 注入 RestTemplate，用于发送 HTTP 请求，与微信接口进行数据交互。
     */
    private final RestTemplate restTemplate;

    /**
     * 通过微信临时登录凭证 code 获取会话信息。
     * 该方法会调用微信的 `jscode2session` 接口，获取用户的 openid、session_key 等信息。
     *
     * @param code 微信客户端返回的临时登录凭证，用于向微信服务器验证用户身份。
     * @return 返回一个包含微信会话信息的对象，包含 openid、session_key 等字段。
     * @throws WeChatApiException 如果在请求过程中出现 HTTP 错误、微信接口返回错误信息或返回结果为空。
     */
    public WeChatSessionResponse getSessionByCode(String code) {
        // 构建调用微信 `jscode2session` 接口的 URL
        String url = buildSessionUrl(code);
        try {
            // 发送 HTTP GET 请求，获取微信接口的响应
            WeChatSessionResponse response = restTemplate.getForObject(url, WeChatSessionResponse.class);
            // 检查响应是否为空
            if (response == null) {
                // 若响应为空，抛出微信接口异常
                throw new WeChatApiException("WeChat interface returns empty response");
            }
            // 检查微信接口是否返回错误码
            if (response.getErrcode() != null && response.getErrcode() != 0) {
                // 若返回错误码，抛出包含错误信息的微信接口异常
                throw new WeChatApiException(response.getErrmsg());
            }
            // 若请求成功，返回微信会话信息
            return response;
        } catch (HttpClientErrorException e) {
            // 若发生 HTTP 请求错误，抛出包含错误信息的微信接口异常
            throw new WeChatApiException("HTTP request fail: " + e.getMessage());
        }
    }

    /**
     * 构建调用微信 `jscode2session` 接口的 URL。
     * 该方法根据微信配置信息和传入的临时登录凭证 code 生成完整的请求 URL。
     *
     * @param code 微信客户端返回的临时登录凭证。
     * @return 返回一个完整的调用微信 `jscode2session` 接口的 URL 字符串。
     */
    private String buildSessionUrl(String code) {
        // 使用 String.format 方法，将微信配置信息和 code 填充到 URL 模板中
        return String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weChatConfig.getAppid(), weChatConfig.getSecret(), code
        );
    }
}