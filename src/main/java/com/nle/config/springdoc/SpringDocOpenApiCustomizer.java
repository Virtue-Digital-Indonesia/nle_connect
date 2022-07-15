package com.nle.config.springdoc;

import com.nle.config.AppConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringDocOpenApiCustomizer implements OpenApiCustomiser, Ordered {

    public static final int DEFAULT_ORDER = 0;

    private int order = DEFAULT_ORDER;

    private final AppConfig appConfig;

    @Override
    public void customise(OpenAPI openApi) {
        Contact contact = new Contact()
            .name(appConfig.getSpringdoc().getName())
            .url(appConfig.getSpringdoc().getUrl())
            .email(appConfig.getSpringdoc().getEmail());

        openApi.info(new Info()
            .contact(contact)
            .title(appConfig.getSpringdoc().getTitle())
            .description(appConfig.getSpringdoc().getDescription())
            .version(appConfig.getSpringdoc().getVersion())
            .license(new License().name("Apache").url("https://www.apache.org/licenses/LICENSE-2.0"))
        );
    }

    @Override
    public int getOrder() {
        return order;
    }
}
