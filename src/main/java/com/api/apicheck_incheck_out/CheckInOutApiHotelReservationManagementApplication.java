package com.api.apicheck_incheck_out;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@OpenAPIDefinition
public class CheckInOutApiHotelReservationManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckInOutApiHotelReservationManagementApplication.class, args);
	}
}