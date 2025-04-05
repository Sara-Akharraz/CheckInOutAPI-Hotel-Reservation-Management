package com.example.demo.Service;

import com.example.demo.Entity.Reservation;
import com.example.demo.Enums.ReservationStatus;
import java.util.List;


public interface ReservationService {
    public Reservation addReservation(Reservation reservation);
    public Reservation updateReservationStatus(Long id, ReservationStatus status);
    public void deleteReservation(Long id);
    public List<Reservation> getAllReservations();
    public Reservation getReservationById(Long id);
}
