package io.chenyiax.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "token")
public class JwtConfig {
    String key;
    Integer validity;
}
