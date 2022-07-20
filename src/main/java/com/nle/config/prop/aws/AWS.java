package com.nle.config.prop.aws;

import lombok.Data;

@Data
public class AWS {
    private Credential credentials;
    private S3 s3;
}
