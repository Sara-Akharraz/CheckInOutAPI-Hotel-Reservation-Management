package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.ChambreDTO;

import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class ChambreMapper {
    @Autowired
    private ChambreReservationRepository chambreReservationRepository;



    public ChambreDTO toDTO(Chambre chambre){
        List<Long> reservationIds = chambre.getChambreReservations()
                .stream()
                .map(ChambreReservation::getId)
                .collect(Collectors.toList());

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
                            .orElseThrow(() -> new RuntimeException("ChambreReservation non trouvée avec l'id : " + id)))
                    .collect(Collectors.toList());
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
