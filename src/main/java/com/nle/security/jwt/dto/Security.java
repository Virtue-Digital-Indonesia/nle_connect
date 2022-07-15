package com.nle.security.jwt.dto;

import com.nle.config.aws.AWS;
import lombok.Data;

@Data
public class Security {
    private Jwt jwt;
    private AWS aws;
}
