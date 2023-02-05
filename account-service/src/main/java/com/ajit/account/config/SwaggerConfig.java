package com.ajit.account.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * The type Swagger config.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "account-service", version = "1.0", description = "Customers accounts details"))
public class SwaggerConfig {
}
