package com.nle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class AppConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setExposedHeaders(Collections.singletonList("*"));
        corsConfiguration.setMaxAge(3600L);
        source.registerCorsConfiguration("/api/**", corsConfiguration);
        source.registerCorsConfiguration("/management/**", corsConfiguration);
        source.registerCorsConfiguration("/v3/api-docs", corsConfiguration);
        source.registerCorsConfiguration("/swagger-ui/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
