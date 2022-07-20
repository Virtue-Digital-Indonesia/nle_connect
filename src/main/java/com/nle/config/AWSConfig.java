package com.nle.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.nle.config.prop.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AWSConfig {
    private final AppProperties appProperties;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(appProperties.getSecurity().getAws().getS3().getAccessKey(),
            appProperties.getSecurity().getAws().getS3().getSecretKey());
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(appProperties.getSecurity().getAws().getS3().getRegion())
            .build();
    }
}
