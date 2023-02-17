package com.nle.config.prop.springdoc;

import com.nle.config.prop.AppProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringDocOpenApiCustomizer implements OpenApiCustomiser, Ordered {

        public static final int DEFAULT_ORDER = 0;

        private int order = DEFAULT_ORDER;

        private final AppProperties appProperties;

        @Override
        public void customise(OpenAPI openApi) {
                // old
                Contact contact = new Contact()
                                .name(appProperties.getSpringdoc().getName())
                                .url(appProperties.getSpringdoc().getUrl())
                                .email(appProperties.getSpringdoc().getEmail());

                openApi.info(new Info()
                                .contact(contact)
                                .title(appProperties.getSpringdoc().getTitle())
                                .description(appProperties.getSpringdoc().getDescription())
                                .version(appProperties.getSpringdoc().getVersion())
                                .license(new License().name("Apache")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
        }

        @Override
        public int getOrder() {
                return order;
        }

        @Bean
        public OpenAPI titleSpringDoc() {
                // new
                Contact contact = new Contact()
                                .name(appProperties.getSpringdoc().getName())
                                .url(appProperties.getSpringdoc().getUrl())
                                .email(appProperties.getSpringdoc().getEmail());
                return new OpenAPI()
                                .info(new Info()
                                                .contact(contact)
                                                .title(appProperties.getSpringdoc().getTitle())
                                                .description(appProperties.getSpringdoc().getDescription())
                                                .version(appProperties.getSpringdoc().getVersion())
                                                .license(new License().name("Apache")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
        }

        @Bean
        public GroupedOpenApi userOpenApi() {
                String paths[] = {
                                "/api/depo-worker-accounts/**",
                                "/api/gate-moves/**",
                                "/api/shipping-lines/**",
                                "/api/inventories/**",
                                "/api/ftp/**",
                                "/api/fleets",
                                "/api/depo-fleet/**",
                                "/api/items/**",
                                "/api/contact-us",
                                "/api/depo/order/**",
                                "/api/item-type",
                                "/api/depo/bank/**",
                                "/api/item-type/iso",
                                "/api/insw-shipping",

                                // depo-owner-controller
                                "/api/register/**",
                                "/api/send-invitation",
                                "/api/reset-password",
                                "/api/forgot-password",
                                "/api/authenticate",
                                "/api/approve-join-request/**",
                                "/api/register/**",
                                "/api/profile",
                                "/api/activate/**",
                                "/api/update",
                                "/api/change-password"
                };
                return GroupedOpenApi
                                .builder()
                                .group("general-user")
                                .pathsToMatch(paths)
                                .build();
        }

        @Bean
        public GroupedOpenApi adminOpenApi() {
                String paths[] = {
                                "/api/admins/**",
                                "/api/switchUser/**",
                                "/api/applicants/**",
                                "/api/fleets/addFleet",
                                "/api/item-type/addItemType",
                                "/api/item-type/iso/addIso",
                                "/api/insw-shipping/add-Insw-Shipping"
                };
                return GroupedOpenApi.builder()
                                .group("secret-admin")
                                .pathsToMatch(paths)
                                .build();
        }

        @Bean
        public GroupedOpenApi guest() {
                String paths[] = {
                                "/api/booking/**",
                                "/api/payment/**",
                                "/api/export/**",
                                "/api/insw/**"};
                return GroupedOpenApi.builder()
                                .group("booking")
                                .pathsToMatch(paths)
                                .build();
        }

}
