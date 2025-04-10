package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChambreDTO {

        @NotNull
        private Long id;

        @NotBlank
        private String nom;

        @NotBlank
        private String etage;

        @NotNull
        private ChambreStatut statut;

        @NotNull
        private double prix;

        private Long id_reservation;


}
