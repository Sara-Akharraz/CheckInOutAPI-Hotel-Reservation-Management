package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.dto.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ReservationService {
    public Reservation addReservation(Reservation reservation,List<Long> chambreIds);
    public Reservation updateReservationStatus(Long id, ReservationStatus status);
    public void deleteReservation(Long id);

    public Reservation getReservationById(Long id);

    public List<Reservation> getReservationsByUserId(Long id);

    public List<Reservation> searchReservations(String search, LocalDate dateDebut, LocalDate dateFin,ReservationStatus status);
    public DetailReservationRequestDTO getReservationDetail(Long reservationId);
    public boolean existsById(Long id) ;
    public User findUserByReservation(Long idReservation);
    public Map<String, Long> getDashboardStats();
}
