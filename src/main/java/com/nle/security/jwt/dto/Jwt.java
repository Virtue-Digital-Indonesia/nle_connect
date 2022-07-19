package com.nle.security.jwt.dto;

import lombok.Data;

@Data
public class Jwt {
    private String base64Secret;
    private long tokenValidityInSeconds;
    private String secret;
    private String temporaryToken;
}
