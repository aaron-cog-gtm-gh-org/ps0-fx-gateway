package com.acme.fx.config;

import com.acme.commons.http.AcmeHttpClient;
import com.acme.commons.http.HttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {
    @Bean(destroyMethod = "close")
    AcmeHttpClient acmeHttpClient(RateProviderProperties properties) {
        return HttpClientFactory.create(properties.baseUrl(), properties.timeout());
    }
}
