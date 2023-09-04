package com.nle;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
@SecurityScheme(name = "nleapi", scheme = "bearer", bearerFormat = "JWT", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@EnableFeignClients
@OpenAPIDefinition(
        servers = {
                @Server(url = "/", description = "Default Server URL")
        }
)
public class NleBackendApplication {

    public static void main(String[] args) throws Exception {
        Connection connLocalhost = DriverManager.getConnection(
                "jdbc:mysql://210.247.245.149:3306/nlebackend?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true",
                "nle",
                ",]O}gtXi|#Y\">");

//        Connection connAPI = DriverManager.getConnection(
//                "jdbc:mysql://210.247.248.133:3308/nlebackend?enabledTLSProtocols=TLSv1.2", "root", "valid1235");

        System.out.println("Connected?");
        SpringApplication.run(NleBackendApplication.class, args);
    }

}
