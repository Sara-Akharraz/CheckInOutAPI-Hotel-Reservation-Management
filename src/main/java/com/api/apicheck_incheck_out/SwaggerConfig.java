package com.api.apicheck_incheck_out;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Reservation Management API: Check-In and Check-Out")
                        .version("1.0.0")
                        .description("Documentation for Hotel Reservation Management API: Check-In and Check-Out"));
    }

}