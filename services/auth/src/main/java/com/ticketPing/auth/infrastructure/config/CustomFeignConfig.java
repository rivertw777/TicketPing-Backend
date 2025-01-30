package com.ticketPing.auth.infrastructure.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomFeignConfig {

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(1000, 12000);
    }
}
