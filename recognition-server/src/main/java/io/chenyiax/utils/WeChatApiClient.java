package io.chenyiax.utils;

import io.chenyiax.configuration.WeChatConfig;
import io.chenyiax.entity.WeChatSessionResponse;
import io.chenyiax.exception.WeChatApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * This component is responsible for interacting with the WeChat API to obtain session information.
 * It uses the provided WeChat configuration and RestTemplate to send requests to the WeChat server.
 */
@Component
@RequiredArgsConstructor
public class WeChatApiClient {
    /**
     * Configuration object for WeChat, containing information such as app ID and secret.
     * This object is used to build the request URL for the WeChat API.
     */
    private final WeChatConfig weChatConfig;
    /**
     * RestTemplate instance used to send HTTP requests to the WeChat API.
     * It simplifies the process of making HTTP requests and handling responses.
     */
    private final RestTemplate restTemplate;

    /**
     * Obtain WeChat session information using a code.
     * This method constructs a request URL based on the provided code, then sends a GET request to the WeChat API.
     * It parses the response and checks for errors. If an error occurs, it throws a WeChatApiException.
     *
     * @param code The code provided by WeChat during the login process.
     * @return A WeChatSessionResponse object containing the session information.
     * @throws WeChatApiException If the WeChat API returns an error, the response is empty, or the HTTP request fails.
     */
    public WeChatSessionResponse getSessionByCode(String code) {
        // Build the request URL for the WeChat session API
        String url = buildSessionUrl(code);
        try {
            // Send a GET request to the WeChat API and parse the response into a WeChatSessionResponse object
            WeChatSessionResponse response = restTemplate.getForObject(url, WeChatSessionResponse.class);
            if (response == null) {
                throw new WeChatApiException("WeChat interface returns empty response");
            }
            // Check the error code returned by the WeChat API
            if (response.getErrcode() != null && response.getErrcode() != 0) {
                throw new WeChatApiException(response.getErrmsg());
            }
            return response;
        } catch (HttpClientErrorException e) {
            // Throw an exception if the HTTP request fails
            throw new WeChatApiException("HTTP request fail: " + e.getMessage());
        }
    }

    /**
     * Build the URL for the WeChat session API.
     * This method uses the app ID and secret from the WeChat configuration, along with the provided code,
     * to construct a complete URL for the WeChat API.
     *
     * @param code The code provided by WeChat during the login process.
     * @return A string representing the complete URL for the WeChat session API.
     */
    private String buildSessionUrl(String code) {
        return String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weChatConfig.getAppid(), weChatConfig.getSecret(), code
        );
    }
}
