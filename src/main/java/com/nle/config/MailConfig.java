package com.nle.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import io.awspring.cloud.ses.SimpleEmailServiceJavaMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
public class MailConfig {
    private final AppConfig appConfig;

    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(appConfig.getSecurity().getAws().getCredentials().getAccessKey(), appConfig.getSecurity().getAws().getCredentials().getSecretKey());
        return AmazonSimpleEmailServiceClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
            .withRegion(Regions.AP_SOUTHEAST_1)
            .build();
    }

    @Bean
    public JavaMailSender javaMailSender(
        AmazonSimpleEmailService amazonSimpleEmailService) {
        return new SimpleEmailServiceJavaMailSender(amazonSimpleEmailService);
    }
}
