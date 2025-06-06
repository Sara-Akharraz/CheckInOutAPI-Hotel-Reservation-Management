package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ChambreType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
        private double prix;

        @NotNull
        private ChambreType type;

        @NotNull
        private int capacite;

        private List<Long> chambreReservationIds;
}
