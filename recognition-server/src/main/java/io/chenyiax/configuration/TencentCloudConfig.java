package io.chenyiax.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tencent.cloud")
@Data
public class TencentCloudConfig {
    private String secretId;
    private String secretKey;
}
