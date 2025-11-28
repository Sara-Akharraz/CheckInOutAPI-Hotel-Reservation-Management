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
public class DetailReservationRequestDTO {
    ReservationDTO reservationDTO;
    List<ReservationServiceRequestDTO> reservationServiceRequestDTO;
    List<ChambreDTO> chambreList;
    private String userFirstName;
    private String userLastName;
    private String userCin;
    private String userPhone;
}
