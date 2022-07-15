package com.nle.config.springdoc;

import lombok.Data;

@Data
public class SpringDocProperties {
    private String title;
    private String description;
    private String version;
    private String name;
    private String url;
    private String email;
}
