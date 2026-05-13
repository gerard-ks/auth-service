package dev.ks.authlayerarchitecture.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description(
                                "JWT Stateless Authentication & Authorization Service"
                        )
                        .version("2.5.3")
                        .contact(new Contact()
                                .name("Auth Service Team")
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("Bearer JWT")
                )
                .components(new Components()
                        .addSecuritySchemes(
                                "Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Access token JWT")
                        )
                );
    }
}
