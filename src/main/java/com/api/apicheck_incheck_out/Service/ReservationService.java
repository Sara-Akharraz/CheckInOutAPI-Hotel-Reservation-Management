package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.DTO.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ReservationService {
    public Reservation addReservation(Reservation reservation,List<Long> chambreIds);
    public Reservation updateReservationStatus(Long id, ReservationStatus status);
    public void deleteReservation(Long id);
//    public List<Reservation> getAllReservations();
    public Reservation getReservationById(Long id);
//    public Reservation addReservationPMS(Long id);
    public List<Reservation> getReservationsByUserId(Long id);

    public List<Reservation> searchReservations(String search, LocalDate dateDebut, LocalDate dateFin,ReservationStatus status);
    public DetailReservationRequestDTO getReservationDetail(Long reservationId);
    public boolean existsById(Long id) ;
    public User findUserByReservation(Long id_resevation);
    public Map<String, Long> getDashboardStats();
}
