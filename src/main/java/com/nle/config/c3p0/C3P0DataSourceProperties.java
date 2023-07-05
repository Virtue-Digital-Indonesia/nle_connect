package com.nle.config.c3p0;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "demo.datasource.c3p0")
public class C3P0DataSourceProperties {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String driverClass;
    private int initialPoolSize;
    private int maxIdleTime;
    private int maxPoolSize;
    private int minPoolSize;
    private int maxStatements;
}
