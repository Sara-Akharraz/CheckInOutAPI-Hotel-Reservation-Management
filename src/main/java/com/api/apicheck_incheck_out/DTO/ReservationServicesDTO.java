package com.api.apicheck_incheck_out.DTO;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
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

    private Long id_reservation;


    private Long id_service;


    private PhaseAjoutService phaseAjoutService;


    private PaiementStatus paiementStatus;
}