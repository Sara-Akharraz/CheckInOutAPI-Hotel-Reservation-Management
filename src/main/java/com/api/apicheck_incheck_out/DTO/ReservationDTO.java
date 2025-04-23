package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
//    @NotNull(message="ID Ne doit pas etre null")
    private Long id;

    private ReservationStatus status;

    private LocalDate date_debut;

    private LocalDate date_fin;
    @NotNull(message="ID client ne doit pas etre null")
    private Long userId;
    @NotNull()
    private List<Long> chambreList;
    @JsonIgnore
    private Long checkinId;
    @JsonIgnore
    private Long checkoutId;
    @JsonIgnore
    private List<Long> factureList;
    @JsonIgnore
    @JsonProperty("serviceList")
    private List<Long> services;
}
