package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChambreStatutManager {

    ReservationRepository reservationRepository;

    ChambreReservationRepository chambreReservationRepository;
    public void occepee(Reservation reservation){
        List<Long> chambreIds = reservation.getChambreReservations().stream()
                .map(ChambreReservation::getId)
                .toList();

        for (Long chambreId : chambreIds) {
            ChambreReservation chambreEntity = chambreReservationRepository.findById(chambreId)
                    .orElseThrow(() -> new ChambreNotFoundException("Chambre non trouvée dans la base de données : " + chambreId));



            chambreEntity.setStatut(ChambreStatut.OCCUPEE);
            chambreEntity.setReservation(reservation);


            chambreReservationRepository.save(chambreEntity);
        }
    }

    public Reservation newStatut(Long id, ReservationStatus status) {
        Optional<Reservation> prevReservation = reservationRepository.findById(id);
        if (prevReservation.isPresent()) {
            Reservation reservation = prevReservation.get();
            reservation.setStatus(status);
            if (status == ReservationStatus.CONFIRMEE) {
                occepee(reservation);
            }
            return reservationRepository.save(reservation);
        } else {
            throw new ReservationNotFoundException("Reservation non trouvée pour l'id :" + id);
        }

    }
}
