package com.nle.config.prop.aws;

import lombok.Data;

@Data
public class S3 {
    private String bucketName;
    private String region;
    private String accessKey;
    private String secretKey;
}
