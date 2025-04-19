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
 * This service class is responsible for interacting with the Tencent Hunyuan API.
 * It initializes the Hunyuan client based on the Tencent Cloud configuration
 * and provides a method for single - round conversations with the model.
 */
@Service
public class HunyuanService {

    private static final Logger logger = LoggerFactory.getLogger(HunyuanService.class);

    /**
     * Default model name used for the Hunyuan API calls.
     */
    private static final String DEFAULT_MODEL = "hunyuan-vision";
    /**
     * Default temperature value used for the Hunyuan API calls.
     * Temperature controls the randomness of the model's output.
     */
    private static final Float DEFAULT_TEMPERATURE = 0.7f;

    /**
     * Tencent Cloud configuration object, injected by Spring.
     * It contains the secret ID and secret key for authentication.
     */
    @Autowired
    private TencentCloudConfig tencentCloudConfig;

    /**
     * Client instance for interacting with the Tencent Hunyuan API.
     */
    private HunyuanClient client;

    /**
     * Initializes the Tencent Hunyuan client after the bean is constructed.
     * Validates the Tencent Cloud configuration and creates a new client instance if the configuration is valid.
     * Throws an exception if the initialization fails.
     */
    @PostConstruct
    public void init() {
        // Validate the Tencent Cloud configuration
        if (!validateConfig()) {
            throw new IllegalStateException("Invalid Tencent Cloud configuration");
        }

        try {
            // Create a new credential object using the secret ID and secret key from the configuration
            Credential cred = new Credential(tencentCloudConfig.getSecretId(), tencentCloudConfig.getSecretKey());

            // Configure the HTTP profile for the client
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("hunyuan.tencentcloudapi.com");

            // Configure the client profile
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // Create a new Hunyuan client instance
            this.client = new HunyuanClient(cred, "", clientProfile);
            logger.info("Tencent Hunyuan client initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Tencent Hunyuan client", e);
            throw new HunYuanException("Failed to initialize Tencent Hunyuan client:" + e);
        }
    }

    /**
     * Validates the Tencent Cloud configuration.
     * Checks if the secret ID and secret key are properly configured.
     *
     * @return true if the configuration is valid, false otherwise.
     */
    private boolean validateConfig() {
        // Check if the secret ID and secret key are not empty
        if (!StringUtils.hasText(tencentCloudConfig.getSecretId()) ||
                !StringUtils.hasText(tencentCloudConfig.getSecretKey())) {
            logger.error("Tencent Cloud credentials are not configured properly");
            return false;
        }
        return true;
    }

    /**
     * Performs a single - round conversation with the Tencent Hunyuan model with parameters.
     * Sends a user prompt and an optional image to the model and returns the model's response.
     *
     * @param prompt    The user's input text.
     * @param imageData The image data to be recognized, which can be in base64 format or a URL.
     * @return The response from the model, or a default message if no response is received.
     */
    public String chat(String prompt, String imageData) {
        try {
            // Create a new chat completion request
            ChatCompletionsRequest req = new ChatCompletionsRequest();

            // Create a new message object
            Message message = new Message();
            message.setRole("user");

            // Build the content parts of the message, including text and image
            Content[] contentParts = buildContentParts(prompt, imageData);
            message.setContents(contentParts);

            // Set the message array in the request
            req.setMessages(new Message[]{message});
            // Set the model name in the request
            req.setModel(DEFAULT_MODEL);
            // Set the temperature in the request
            req.setTemperature(DEFAULT_TEMPERATURE);

            // Send the request to the Hunyuan API and get the response
            ChatCompletionsResponse resp = client.ChatCompletions(req);

            // Check if the response contains any choices
            if (resp.getChoices() != null && resp.getChoices().length > 0) {
                return resp.getChoices()[0].getMessage().getContent();
            }

            return "No response from the model";
        } catch (TencentCloudSDKException e) {
            logger.error("Tencent Hunyuan API call failed. ErrorCode: {}, RequestId: {}",
                    e.getErrorCode(), e.getRequestId(), e);
            throw new HunYuanException("Failed to call Tencent Hunyuan API:" + e);
        }
    }

    /**
     * Builds an array of content parts for the message, including text and image content.
     * Uses Java Stream and Optional to handle nullable inputs.
     *
     * @param textPrompt The user's text prompt.
     * @param imageData  The image data to be recognized.
     * @return An array of Content objects representing the message content.
     */
    private Content[] buildContentParts(String textPrompt, String imageData) {
        return Stream.of(
                        // Create text content if the text prompt is not empty
                        Optional.ofNullable(textPrompt)
                                .filter(text -> !text.isEmpty())
                                .map(this::createTextContent),
                        // Create image content if the image data is not empty
                        Optional.ofNullable(imageData)
                                .filter(data -> !data.isEmpty())
                                .map(this::createImageContent)
                )
                // Filter out empty Optional objects
                .filter(Optional::isPresent)
                // Extract the value from the Optional objects
                .map(Optional::get)
                // Collect the results into an array
                .toArray(Content[]::new);
    }

    /**
     * Creates a text content object for the message.
     *
     * @param text The text content.
     * @return A Content object representing the text content.
     */
    private Content createTextContent(String text) {
        Content content = new Content();
        content.setType("text");
        content.setText(text);
        return content;
    }

    /**
     * Creates an image content object for the message.
     * Formats the image data into a URL and sets it in the content object.
     *
     * @param imageData The image data, which can be a URL or base64 - encoded data.
     * @return A Content object representing the image content.
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
     * Formats the image data into a valid URL.
     * If the image data starts with "http", it is considered a URL and returned as is.
     * Otherwise, it is assumed to be base64 - encoded data and prefixed with the appropriate data URI scheme.
     *
     * @param imageData The image data to be formatted.
     * @return A formatted image URL.
     */
    private String formatImageUrl(String imageData) {
        return imageData.startsWith("http") ? imageData : "data:image/jpeg;base64," + imageData;
    }
}