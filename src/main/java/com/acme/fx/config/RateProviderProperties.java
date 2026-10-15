package com.acme.fx.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "acme.rate-provider")
public record RateProviderProperties(String baseUrl, Duration timeout) {
}
