package com.example.monitor.infra.converter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {
    @Bean
    public BrandConverterRule brandConverterRuleMap() {
        return new BrandConverterRule();
    }
}
