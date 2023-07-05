package com.nle.config.c3p0;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "hibernate.c3p0.datasource")
public class C3P0DataSourceProperties {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String driverClass;
    private int initialPoolSize;
    private int minPoolSize;
    private int maxPoolSize;
    private int maxIdleTime;
    private int maxStatements;
    private int acquireIncrement;
    private int timeout;
}
