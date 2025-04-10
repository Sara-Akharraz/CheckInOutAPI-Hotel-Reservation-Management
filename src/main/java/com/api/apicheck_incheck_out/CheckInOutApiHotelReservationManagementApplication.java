package com.api.apicheck_incheck_out;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
		(
				scanBasePackages = "com.api.apicheck_incheck_out"
		)
@EnableJpaRepositories(basePackages = "com.api.apicheck_incheck_out")
@EntityScan(basePackages = "com.api.apicheck_incheck_out")

@OpenAPIDefinition
public class CheckInOutApiHotelReservationManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckInOutApiHotelReservationManagementApplication.class, args);
	}

}
