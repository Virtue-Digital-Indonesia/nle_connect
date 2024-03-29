package com.nle.security.jwt.dto;

import com.nle.config.prop.aws.AWS;
import com.nle.config.prop.ftp.Ftp;
import com.nle.config.prop.tax.TaxMinistry;
import lombok.Data;

@Data
public class Security {
    private Jwt jwt;
    private AWS aws;
    private Ftp ftp;
    private TaxMinistry taxMinistry;
}
