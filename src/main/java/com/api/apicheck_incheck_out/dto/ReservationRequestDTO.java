package com.api.apicheck_incheck_out.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReservationRequestDTO {
    private ReservationDTO reservationDTO;
    private List<Long> chambresId;
}
