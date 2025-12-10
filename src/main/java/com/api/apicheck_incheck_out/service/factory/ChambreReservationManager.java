package com.api.apicheck_incheck_out.service.factory;


import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChambreReservationManager {

    ReservationRepository reservationRepository;
    ChambreRepository chambreRepository;
    ChambreReservationRepository chambreReservationRepository;
    ChambreMapper chambreMapper;

    public Reservation processChambreReservation(Reservation reservation, List<Long> chambreIds) {
        reservation.setStatus(ReservationStatus.EN_ATTENTE);
        Reservation savedReservation = reservationRepository.save(reservation);

        for (Long chambreId : chambreIds) {
            Chambre chambreEntity = chambreRepository.findById(chambreId)
                    .orElseThrow(() -> new ChambreNotFoundException("Chambre non trouvée dans la base de données : " + chambreId));

            ChambreReservation chambreReservation = new ChambreReservation();
            chambreReservation.setChambre(chambreEntity);
            chambreReservation.setReservation(savedReservation);
            chambreReservation.setStatut(ChambreStatut.RESERVED);

            chambreReservationRepository.save(chambreReservation);

        }
    return savedReservation;
    }

    public List<ChambreDTO> findRoomsOfReservation(Reservation reservation){
        List<Chambre> chambres = reservation.getChambreReservations().stream()
                .map(cr -> cr.getChambre())
                .toList();

        return chambres.stream()
                .map(chambreMapper::toDTO)
                .toList();
    }
}
