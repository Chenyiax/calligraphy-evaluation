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
 * This class is a configuration class for Spring's RestTemplate.
 * It is used to define and customize the RestTemplate bean, including setting timeouts
 * and adding a custom message converter to support parsing JSON data in the 'text/plain' format.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and configures a RestTemplate bean.
     * This method uses the RestTemplateBuilder provided by Spring Boot to create a RestTemplate instance.
     * It adds a custom MappingJackson2HttpMessageConverter to support parsing JSON data in both
     * 'application/json' and 'text/plain' formats. Additionally, it sets the connection timeout
     * and read timeout for the RestTemplate.
     *
     * @param builder The RestTemplateBuilder provided by Spring Boot, used to build the RestTemplate.
     * @return A configured RestTemplate instance.
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