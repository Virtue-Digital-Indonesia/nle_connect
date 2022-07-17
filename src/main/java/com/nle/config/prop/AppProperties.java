package com.nle.config.prop;


import com.nle.config.prop.springdoc.SpringDocProperties;
import com.nle.config.prop.url.Url;
import com.nle.security.jwt.dto.Security;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private Security security;
    private SpringDocProperties springdoc;
    private Url url;
}

