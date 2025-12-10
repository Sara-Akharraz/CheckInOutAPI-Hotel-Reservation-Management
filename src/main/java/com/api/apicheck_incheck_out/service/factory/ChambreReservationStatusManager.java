package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidReservationStatusException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChambreReservationStatusManager {
    private final ChambreReservationRepository chambreReservationRepository;
    private final ReservationRepository reservationRepository;
    private final ChambreReservationFinder finder;
    private final ReservationServicesFinder resfinder;

    public ChambreReservationStatusManager(ChambreReservationRepository chambreReservationRepository, ReservationRepository reservationRepository, ChambreReservationFinder finder, ReservationServicesFinder resfiner) {
        this.chambreReservationRepository = chambreReservationRepository;
        this.reservationRepository = reservationRepository;
        this.finder = finder;
        this.resfinder = resfiner;
    }
    public void setChambreOccupee(Long idReservation){
        Reservation reservation=resfinder.findReservationById(idReservation);
        validateReservationsStatus(reservation,ReservationStatus.CONFIRMEE);
        List<ChambreReservation> chambreReservations=finder.findChambreReservationByReservationId(idReservation);

        chambreReservations.forEach(chambreReservation ->updateChambreReservationStatus(chambreReservation,ChambreStatut.OCCUPEE,reservation) );
    }
    private void validateReservationsStatus(Reservation res, ReservationStatus status){
        if (res.getStatus() !=status) {
            throw new InvalidReservationStatusException( String.format("La réservation n'est pas %s, statut actuel : %s",
                    status, res.getStatus()));
        }
    }
    private void updateChambreReservationStatus(
            ChambreReservation chambreReservation,
            ChambreStatut newStatus,
            Reservation reservation
    ) {
        chambreReservation.setStatut(newStatus);
        chambreReservation.setReservation(reservation);
        chambreReservationRepository.save(chambreReservation);
    }
    public void setChambreDisponible(Long idReservation){
        Reservation reservation=resfinder.findReservationById(idReservation);
        validateReservationsStatus(reservation,ReservationStatus.CONFIRMEE);
        List<ChambreReservation> chambreReservations=finder.findChambreReservationsByReservation(reservation);

        chambreReservations.forEach(chambreReservation ->
                updateChambreReservationStatus(chambreReservation, ChambreStatut.DISPONIBLE, null)
        );
        updateReservationStatus(reservation,ReservationStatus.TERMINEE);
    }
    public void updateReservationStatus(Reservation reservation, ReservationStatus newStatus) {
        reservation.setStatus(newStatus);
        reservationRepository.save(reservation);
    }
    public void setChambreReserved(Long idChambre,Long idReservation){
        ChambreReservation chambreReservation = finder.findChambreReservation(idReservation, idChambre);
        Reservation reservation = resfinder.findReservationById(idReservation);

        validateReservationsStatus(reservation,ReservationStatus.EN_ATTENTE);
        updateChambreReservationStatus(chambreReservation,ChambreStatut.RESERVED,reservation);
    }
}
