package com.api.apicheck_incheck_out;

import com.api.apicheck_incheck_out.Service.Impl.EmailSenderService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@OpenAPIDefinition
public class CheckInOutApiHotelReservationManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckInOutApiHotelReservationManagementApplication.class, args);
	}



}
