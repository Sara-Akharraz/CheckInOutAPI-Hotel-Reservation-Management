package com.api.apicheck_incheck_out.dto;

import com.api.apicheck_incheck_out.enums.CheckInStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDTO {
        @NotNull
        private Long id;

        private LocalDate dateCheckIn;

        private CheckInStatus status;


        private Long idReservation;


        private Long idDocumentScan;

}
