package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Entity.Chambre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
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
