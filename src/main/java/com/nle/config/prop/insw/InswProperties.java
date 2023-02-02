package com.nle.config.prop.insw;

import lombok.Data;

@Data
public class InswProperties {
    private String clientId;
    private String clientSecret;
    private String provisionKey;
    private String redirectUri;
    private String accessToken;
    private String refreshToken;
}
