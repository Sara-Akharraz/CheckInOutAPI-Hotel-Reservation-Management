package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationConfirmationManager {
    private final ReservationRepository reservationRepository;
    private final ChambreReservationRepository chambreReservationRepository;
    private final ReservationServiceRepository reservationServiceRepository;

    public ReservationConfirmationManager(
            ReservationRepository reservationRepository,
            ChambreReservationRepository chambreReservationRepository,
            ReservationServiceRepository reservationServiceRepository) {
        this.reservationRepository = reservationRepository;
        this.chambreReservationRepository = chambreReservationRepository;
        this.reservationServiceRepository = reservationServiceRepository;
    }

    public void confirmReservation(Reservation reservation) {
        updateReservationStatus(reservation);
        updateChambreReservations(reservation);
        updateReservationServices(reservation);
    }

    private void updateReservationStatus(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        reservationRepository.save(reservation);
    }

    private void updateChambreReservations(Reservation reservation) {
        List<ChambreReservation> chambreReservations = reservation.getChambreReservations();

        chambreReservations.stream()
                .filter(cr -> cr.getReservation().getId().equals(reservation.getId()))
                .forEach(cr -> cr.setStatut(ChambreStatut.OCCUPEE));

        chambreReservationRepository.saveAll(chambreReservations);
    }

    private void updateReservationServices(Reservation reservation) {
        List<ReservationServices> reservationServices = reservation.getServiceList();

        reservationServices.stream()
                .filter(rs -> rs.getReservation().getId().equals(reservation.getId()))
                .forEach(rs -> rs.setPaiementStatus(PaiementStatus.PAYE));

        reservationServiceRepository.saveAll(reservationServices);
    }
}
