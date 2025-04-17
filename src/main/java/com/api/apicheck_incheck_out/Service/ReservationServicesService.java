package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.ReservationServices;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;

import java.util.List;

public interface ReservationServicesService {
    public List<ReservationServices> getAllServicesByReservation(Long id_reservation);
    public List<ReservationServices> getServicesByPhase(Long id_reservation, PhaseAjoutService phase);

}
