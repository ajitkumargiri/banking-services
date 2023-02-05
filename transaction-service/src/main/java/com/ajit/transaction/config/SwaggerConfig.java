package com.ajit.transaction.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "transaction-service", version = "1.0", description = "Accounts transaction details"))
public class SwaggerConfig {
    //we can use this separate config file  for more advance configurations
}
