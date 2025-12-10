package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReservationServicesFinder {

    private final ReservationRepository reservationRepository;
    private final ChambreReservationRepository chambreReservationRepository;

    public ReservationServicesFinder(ChambreReservationRepository chambreReservationRepository, ReservationRepository reservationRepository) {
        this.chambreReservationRepository = chambreReservationRepository;
        this.reservationRepository = reservationRepository;
    }

    public Reservation findReservationById(Long idReservation){
        return reservationRepository.findById(idReservation).orElseThrow(()->new ReservationNotFoundException("Reservation non trouvée avec l'id : " + idReservation));
    }
    public Set<Long> extractExistingServiceIds(Reservation res){
        return res.getServiceList().stream()
                .map(rs->rs.getService().getId())
                .collect(Collectors.toSet());
    }
    public Reservation verifyForSameRooms(Reservation reservation, List<Long> chambreIds){
        List<ChambreReservation> existingChambreReservations = chambreReservationRepository.findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(
                chambreIds, reservation.getDateDebut(), reservation.getDateFin());

        if (!existingChambreReservations.isEmpty()) {
            throw new EntityExistsException("Une réservation existe déjà pour ces chambres avec les mêmes dates.");
        }
        return reservation;
    }
}