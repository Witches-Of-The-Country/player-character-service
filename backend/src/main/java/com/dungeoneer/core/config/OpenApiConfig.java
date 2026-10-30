package com.dungeoneer.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Dungeoneer - Player Character Service API",
                version = "v1.1.0",
                description = "Character sheet management, D&D 5e/2024 rules engine, and reference data"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Direct Player Character Service (Port 8080)"),
                @Server(url = "http://localhost:9090", description = "API Gateway (Port 9090)")
        },
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter JWT Bearer token issued by Auth Service"
)
public class OpenApiConfig {

    @Bean
    public OperationCustomizer removeXUserIdFromSwagger() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() != null) {
                operation.getParameters().removeIf(param -> "X-User-Id".equalsIgnoreCase(param.getName()));
            }
            return operation;
        };
    }
}
