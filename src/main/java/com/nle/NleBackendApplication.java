package com.nle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NleBackendApplication.class, args);
    }

}
