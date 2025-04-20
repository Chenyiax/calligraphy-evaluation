package io.chenyiax.service;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.*;

import io.chenyiax.configuration.TencentCloudConfig;
import io.chenyiax.exception.HunYuanException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * HunyuanService 是一个 Spring 服务类，用于与腾讯混元大模型进行交互。
 * 该类负责初始化腾讯云客户端，验证配置信息，并提供与模型进行聊天的功能。
 */
@Service
public class HunyuanService {

    /**
     * 日志记录器，用于记录服务运行过程中的信息和错误。
     */
    private static final Logger logger = LoggerFactory.getLogger(HunyuanService.class);

    /**
     * 默认使用的腾讯混元模型名称。
     */
    private static final String DEFAULT_MODEL = "hunyuan-vision";

    /**
     * 默认的温度参数，用于控制模型生成结果的随机性。
     */
    private static final Float DEFAULT_TEMPERATURE = 0.7f;

    /**
     * 自动注入腾讯云配置信息，包含密钥等关键信息。
     */
    @Autowired
    private TencentCloudConfig tencentCloudConfig;

    /**
     * 腾讯混元客户端，用于与腾讯混元 API 进行通信。
     */
    private HunyuanClient client;

    /**
     * 初始化方法，在 Bean 初始化完成后自动调用。
     * 该方法验证腾讯云配置信息，并初始化腾讯混元客户端。
     */
    @PostConstruct
    public void init() {
        // 验证腾讯云配置信息是否有效
        if (!validateConfig()) {
            // 若配置无效，抛出异常并终止初始化
            throw new IllegalStateException("Invalid Tencent Cloud configuration");
        }

        try {
            // 创建腾讯云凭证对象，使用配置中的密钥信息
            Credential cred = new Credential(tencentCloudConfig.getSecretId(), tencentCloudConfig.getSecretKey());

            // 创建 HTTP 配置对象，并设置腾讯混元 API 的端点
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("hunyuan.tencentcloudapi.com");

            // 创建客户端配置对象，并将 HTTP 配置对象设置到其中
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 初始化腾讯混元客户端
            this.client = new HunyuanClient(cred, "", clientProfile);
            // 记录客户端初始化成功的日志
            logger.info("Tencent Hunyuan client initialized successfully");
        } catch (Exception e) {
            // 记录客户端初始化失败的日志，并抛出自定义异常
            logger.error("Failed to initialize Tencent Hunyuan client", e);
            throw new HunYuanException("Failed to initialize Tencent Hunyuan client:" + e);
        }
    }

    /**
     * 验证腾讯云配置信息是否有效。
     * 检查配置中的密钥信息是否存在。
     *
     * @return 若配置有效返回 true，否则返回 false。
     */
    private boolean validateConfig() {
        // 检查配置中的密钥信息是否为空
        if (!StringUtils.hasText(tencentCloudConfig.getSecretId()) ||
                !StringUtils.hasText(tencentCloudConfig.getSecretKey())) {
            // 若密钥信息为空，记录错误日志并返回 false
            logger.error("Tencent Cloud credentials are not configured properly");
            return false;
        }
        return true;
    }

    /**
     * 与腾讯混元模型进行聊天的方法。
     * 构建聊天请求，调用腾讯混元 API，并处理响应结果。
     *
     * @param prompt 文本提示信息，用于向模型提问。
     * @param imageData 图像数据，可用于视觉相关的模型交互。
     * @return 模型返回的聊天响应内容，若没有响应则返回默认提示信息。
     * @throws HunYuanException 若调用腾讯混元 API 失败，抛出该异常。
     */
    public String chat(String prompt, String imageData) {
        try {
            // 创建聊天完成请求对象
            ChatCompletionsRequest req = new ChatCompletionsRequest();

            // 创建消息对象，并设置消息角色为用户
            Message message = new Message();
            message.setRole("user");

            // 构建消息内容部分，包含文本和图像信息
            Content[] contentParts = buildContentParts(prompt, imageData);
            message.setContents(contentParts);

            // 将消息对象设置到请求对象中
            req.setMessages(new Message[]{message});
            // 设置请求使用的模型名称为默认模型
            req.setModel(DEFAULT_MODEL);
            // 设置请求的温度参数为默认值
            req.setTemperature(DEFAULT_TEMPERATURE);

            // 调用腾讯混元 API 发送请求并获取响应
            ChatCompletionsResponse resp = client.ChatCompletions(req);

            // 检查响应中是否有有效的选择结果
            if (resp.getChoices() != null && resp.getChoices().length > 0) {
                // 若有有效结果，返回第一个选择的消息内容
                return resp.getChoices()[0].getMessage().getContent();
            }

            // 若没有有效结果，返回默认提示信息
            return "No response from the model";
        } catch (TencentCloudSDKException e) {
            // 记录调用腾讯混元 API 失败的日志，并抛出自定义异常
            logger.error("Tencent Hunyuan API call failed. ErrorCode: {}, RequestId: {}",
                    e.getErrorCode(), e.getRequestId(), e);
            throw new HunYuanException("Failed to call Tencent Hunyuan API:" + e);
        }
    }

    /**
     * 构建聊天消息的内容部分，包含文本和图像信息。
     * 使用 Java 流和 Optional 处理可能为空的文本和图像数据。
     *
     * @param textPrompt 文本提示信息。
     * @param imageData 图像数据。
     * @return 包含文本和图像内容的 Content 数组。
     */
    private Content[] buildContentParts(String textPrompt, String imageData) {
        return Stream.of(
                        // 创建文本内容，如果文本提示不为空
                        Optional.ofNullable(textPrompt)
                                .filter(text -> !text.isEmpty())
                                .map(this::createTextContent),
                        // 创建图像内容，如果图像数据不为空
                        Optional.ofNullable(imageData)
                                .filter(data -> !data.isEmpty())
                                .map(this::createImageContent)
                )
                // 过滤掉 Optional 中的空值
                .filter(Optional::isPresent)
                // 获取 Optional 中的实际值
                .map(Optional::get)
                // 将流转换为 Content 数组
                .toArray(Content[]::new);
    }

    /**
     * 创建文本内容对象。
     * 设置内容类型为文本，并将文本信息设置到内容对象中。
     *
     * @param text 文本信息。
     * @return 包含文本信息的 Content 对象。
     */
    private Content createTextContent(String text) {
        Content content = new Content();
        content.setType("text");
        content.setText(text);
        return content;
    }

    /**
     * 创建图像内容对象。
     * 设置内容类型为图像 URL，并将格式化后的图像 URL 设置到内容对象中。
     *
     * @param imageData 图像数据。
     * @return 包含图像信息的 Content 对象。
     */
    private Content createImageContent(String imageData) {
        Content content = new Content();
        content.setType("image_url");

        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setUrl(formatImageUrl(imageData));
        content.setImageUrl(imageUrl);

        return content;
    }

    /**
     * 格式化图像 URL。
     * 若图像数据已经是合法的 URL，则直接返回；否则，将其转换为 Base64 编码的 URL。
     *
     * @param imageData 图像数据。
     * @return 格式化后的图像 URL。
     */
    private String formatImageUrl(String imageData) {
        return imageData.startsWith("http") ? imageData : "data:image/jpeg;base64," + imageData;
    }
}