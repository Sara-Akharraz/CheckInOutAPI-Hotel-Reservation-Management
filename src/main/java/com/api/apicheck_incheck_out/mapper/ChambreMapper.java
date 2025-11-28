package com.api.apicheck_incheck_out.mapper;

import com.api.apicheck_incheck_out.dto.ChambreDTO;

import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.*;

import org.springframework.stereotype.Component;

import java.util.List;



@Component
public class ChambreMapper {

    private final ChambreReservationRepository chambreReservationRepository;

    public ChambreMapper(ChambreReservationRepository chambreReservationRepository) {
        this.chambreReservationRepository = chambreReservationRepository;
    }


    public ChambreDTO toDTO(Chambre chambre){
        List<Long> reservationIds = chambre.getChambreReservations()
                .stream()
                .map(ChambreReservation::getId)
                .toList();

        return new ChambreDTO(
              chambre.getId(),
              chambre.getNom(),
              chambre.getEtage(),
              chambre.getPrix(),
              chambre.getType(),
              chambre.getCapacite(),
              reservationIds
        );
    }
    public Chambre toEntity(ChambreDTO chambreDTO){
        List<ChambreReservation> reservations = null;

        if (chambreDTO.getChambreReservationIds() != null && !chambreDTO.getChambreReservationIds().isEmpty()) {
            reservations = chambreDTO.getChambreReservationIds().stream()
                    .map(id -> chambreReservationRepository.findById(id)
                            .orElseThrow(() -> new ChambreReservationNotFoundException("ChambreReservation non trouvée avec l'id : " + id)))
                    .toList();
        }

        return new Chambre(
                chambreDTO.getId(),
                chambreDTO.getNom(),
                chambreDTO.getEtage(),
                chambreDTO.getPrix(),
                chambreDTO.getType(),
                chambreDTO.getCapacite(),
                reservations
        );
    }
}
