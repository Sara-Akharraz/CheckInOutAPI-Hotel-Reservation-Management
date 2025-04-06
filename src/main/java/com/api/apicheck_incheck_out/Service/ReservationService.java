package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import java.util.List;


public interface ReservationService {
    public Reservation addReservation(Reservation reservation);
    public Reservation updateReservationStatus(Long id, ReservationStatus status);
    public void deleteReservation(Long id);
    public List<Reservation> getAllReservations();
    public Reservation getReservationById(Long id);
    public Reservation addReservationPMS(Long id);
}
