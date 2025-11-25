package com.api.apicheck_incheck_out.dto;

import com.api.apicheck_incheck_out.enums.ChambreType;
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
