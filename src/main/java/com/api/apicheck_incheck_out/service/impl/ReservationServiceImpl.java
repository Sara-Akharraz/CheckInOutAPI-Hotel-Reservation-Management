package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.dto.*;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;

import com.api.apicheck_incheck_out.repository.*;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationManager;
import com.api.apicheck_incheck_out.service.factory.ChambreStatutManager;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import com.api.apicheck_incheck_out.service.factory.ReservationFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;
    private final CheckInRepository checkInRepository;
    private final CheckOutRepository checkOutRepository;

    private final ReservationServicesFinder reservationServicesFinder;
    private final ChambreReservationManager chambreReservationManager;
    private final ChambreStatutManager chambreStatutManager;
    private final ReservationFinder reservationFinder;

    @Override
    public Reservation addReservation(Reservation reservation, List<Long> chambreIds) {
        reservationServicesFinder.verifyForSameRooms(reservation,chambreIds);
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        return chambreReservationManager.processChambreReservation(reservation,chambreIds);
    }

    //public void updateChambreToUccupee(Reservation reservation){
      //  chambreStatutManager.occepee(reservation);
    //}
    @Override
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        return chambreStatutManager.newStatut(id,status);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationFinder.existante(id);
        reservationRepository.deleteById(id);
    }


    @Override
    public Reservation getReservationById(Long id) {
        return reservationFinder.findById(id);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationFinder.findByUser(userId);
    }

    @Override
    public List<Reservation> searchReservations(String search, LocalDate dateDebut, LocalDate dateFin, ReservationStatus status) {
        return reservationFinder.search(search, dateDebut, dateFin, status);
    }

    public DetailReservationRequestDTO getReservationDetail(Long reservationId) {
        Reservation reservation = reservationFinder.findById(reservationId);
        ReservationDTO reservationDTO = reservationMapper.toDTO(reservation);
        List<ReservationServiceRequestDTO> serviceDTOs = reservationFinder.findServices(reservation);
        return reservationFinder.details(reservation, reservationDTO, serviceDTOs, chambreReservationManager.findRoomsOfReservation(reservation));
    }

    @Override
    public boolean existsById(Long id) {
        return reservationRepository.existsById(id);
    }

    @Override
    public User findUserByReservation(Long idReservation){
        return reservationFinder.findUserOfReservation(idReservation);
    }

    @Override
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("reservations", reservationRepository.count());
        stats.put("checkins", checkInRepository.count());
        stats.put("checkouts", checkOutRepository.count());
        return stats;
    }

}



