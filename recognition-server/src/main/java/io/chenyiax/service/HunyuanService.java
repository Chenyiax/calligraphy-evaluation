package io.chenyiax.service;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.*;

import io.chenyiax.configuration.TencentCloudConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class HunyuanService {
    private static final Logger logger = LoggerFactory.getLogger(HunyuanService.class);

    private static final String DEFAULT_MODEL = "hunyuan-vision";
    private static final Float DEFAULT_TEMPERATURE = 0.7f;

    @Autowired
    private TencentCloudConfig tencentCloudConfig;

    private HunyuanClient client;

    @PostConstruct
    public void init() {
        if (!validateConfig()) {
            throw new IllegalStateException("Invalid Tencent Cloud configuration");
        }

        try {
            Credential cred = new Credential(tencentCloudConfig.getSecretId(), tencentCloudConfig.getSecretKey());

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("hunyuan.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            this.client = new HunyuanClient(cred, "", clientProfile);
            logger.info("Tencent Hunyuan client initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Tencent Hunyuan client", e);
            throw new RuntimeException("Failed to initialize Tencent Hunyuan client", e);
        }
    }

    private boolean validateConfig() {
        if (!StringUtils.hasText(tencentCloudConfig.getSecretId()) ||
                !StringUtils.hasText(tencentCloudConfig.getSecretKey())) {
            logger.error("Tencent Cloud credentials are not configured properly");
            return false;
        }
        return true;
    }

    /**
     * 带参数的单轮对话
     *
     * @param prompt    用户输入
     * @param imageData 待识别图像,支持base64或url
     * @return 模型响应
     */
    public String chat(String prompt, String imageData) {
        try {
            ChatCompletionsRequest req = new ChatCompletionsRequest();

            Message message = new Message();
            message.setRole("user");

            // 构建多部分内容
            Content[] contentParts = buildContentParts(prompt, imageData);
            message.setContents(contentParts);

            req.setMessages(new Message[]{message});
            req.setModel(DEFAULT_MODEL);
            req.setTemperature(DEFAULT_TEMPERATURE);

            ChatCompletionsResponse resp = client.ChatCompletions(req);

            if (resp.getChoices() != null && resp.getChoices().length > 0) {
                return resp.getChoices()[0].getMessage().getContent();
            }

            return "No response from the model";
        } catch (TencentCloudSDKException e) {
            logger.error("Tencent Hunyuan API call failed. ErrorCode: {}, RequestId: {}",
                    e.getErrorCode(), e.getRequestId(), e);
            throw new RuntimeException("Failed to call Tencent Hunyuan API", e);
        }
    }

    // 流处理真好用
    private Content[] buildContentParts(String textPrompt, String imageData) {
        return Stream.of(
                        Optional.ofNullable(textPrompt)
                                .filter(text -> !text.isEmpty())
                                .map(this::createTextContent),
                        Optional.ofNullable(imageData)
                                .filter(data -> !data.isEmpty())
                                .map(this::createImageContent)
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(Content[]::new);
    }

    // 创建文本内容的辅助方法
    private Content createTextContent(String text) {
        Content content = new Content();
        content.setType("text");
        content.setText(text);
        return content;
    }

    // 创建图片内容的辅助方法
    private Content createImageContent(String imageData) {
        Content content = new Content();
        content.setType("image_url");

        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setUrl(formatImageUrl(imageData));
        content.setImageUrl(imageUrl);

        return content;
    }

    // 格式化图片URL的辅助方法
    private String formatImageUrl(String imageData) {
        return imageData.startsWith("http") ? imageData : "data:image/jpeg;base64," + imageData;
    }
}