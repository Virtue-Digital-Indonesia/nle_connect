package com.nle.config;


import com.nle.config.springdoc.SpringDocProperties;
import com.nle.security.jwt.dto.Security;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private Security security;
    private SpringDocProperties springdoc;
}

