package com.api.apicheck_incheck_out.dto;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationServicesDTO {
    @NotNull
    private Long id;

    private Long idReservation;


    private Long idService;


    private PhaseAjoutService phaseAjoutService;


    private PaiementStatus paiementStatus;
}
