package com.api.apicheck_incheck_out.PMSMock.Service;

import com.api.apicheck_incheck_out.Dto.ReservationDTO;

import java.util.List;

public interface PMSService {
    ReservationDTO demanderReservation(ReservationDTO reservationDTO);
    ReservationDTO getDemandeReservationById(Long id);
    List<ReservationDTO> getAllDemandeReservation();
    ReservationDTO updateDemandeReservation(Long id,ReservationDTO reservationDTO);
}
