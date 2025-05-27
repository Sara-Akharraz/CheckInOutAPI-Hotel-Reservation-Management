package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Enums.CheckInStatus;
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


        private Long id_reservation;


        private Long id_documentScan;

}
