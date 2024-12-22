package net.codejava;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudFrontConfig {
    @Value("${cdn.url}")
    private String cdnUrl;
    
    @Bean
    public String cdnUrl() {
        return cdnUrl;
    }
}
