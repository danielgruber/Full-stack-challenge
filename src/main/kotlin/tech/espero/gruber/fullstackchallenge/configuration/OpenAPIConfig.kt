package tech.espero.gruber.fullstackchallenge.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenAPIConfig {
    @Bean
    fun springShopOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Vending machine API")
                    .description("Spring vending machine sample application")
                    .version("v0.0.1")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            ).components(
                Components().addSecuritySchemes(
                    "JWT", SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("jwt")
                        .`in`(SecurityScheme.In.HEADER)
                        .name("Authorization")
                )
            ).addSecurityItem(SecurityRequirement().addList("JWT"));
    }
}
