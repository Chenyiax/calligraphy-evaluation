package io.chenyiax.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

/**
 * 该类是 Spring 的 RestTemplate 配置类。
 * 用于定义和自定义 RestTemplate bean，包括设置超时时间
 * 并添加自定义消息转换器以支持解析 'text/plain' 格式的 JSON 数据。
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 创建并配置一个 RestTemplate bean。
     * 此方法使用 Spring Boot 提供的 RestTemplateBuilder 来创建 RestTemplate 实例。
     * 它添加了一个自定义的 MappingJackson2HttpMessageConverter，以支持解析 'application/json' 和 'text/plain' 格式的 JSON 数据。
     * 此外，还设置了 RestTemplate 的连接超时时间和读取超时时间。
     *
     * @param builder Spring Boot 提供的 RestTemplateBuilder，用于构建 RestTemplate。
     * @return 一个配置好的 RestTemplate 实例。
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                new MediaType("text", "plain", StandardCharsets.UTF_8)
        ));

        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .additionalMessageConverters(converter)
                .build();
    }
}