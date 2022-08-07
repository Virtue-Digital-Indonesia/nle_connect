package com.nle.config.openfeign;

import org.springframework.context.annotation.Bean;

public class TaxMinistryClientConfiguration {
    @Bean
    public TaxMinistryInterceptor jphInterceptor() {
        return new TaxMinistryInterceptor();
    }
}
