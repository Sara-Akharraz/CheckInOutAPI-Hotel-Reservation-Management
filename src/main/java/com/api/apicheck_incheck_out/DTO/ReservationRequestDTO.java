package com.api.apicheck_incheck_out.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDTO {
    private ReservationDTO reservationDTO;
    private List<Long> chambresId;
}
