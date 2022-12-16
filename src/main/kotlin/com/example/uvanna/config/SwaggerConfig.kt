package com.example.uvanna.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {

    @Bean
    fun publicApiV1(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("uvanna-api")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun aniFoxOpenAPI(): OpenAPI? {
        return OpenAPI()
            .info(
                Info()
                    .title("Uvanna official API")
                        .contact(Contact().email("denis.akhunov123@gmail.com").name("Akhunov Denis")
                        )
                    .description("Uvanna Application API")
                    .version("v1")
                    .license(License().name("GPL 2.0").url("#"))
            )
    }

}