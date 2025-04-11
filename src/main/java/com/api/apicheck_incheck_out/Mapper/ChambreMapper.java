package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.ChambreDTO;

import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ChambreMapper {
    @Autowired
    private ReservationRepository reservationRepository;



    public ChambreDTO toDTO(Chambre chambre){

        Long id_reservation=(chambre.getReservation() !=null)? chambre.getReservation().getId():null;

        return new ChambreDTO(
              chambre.getId(),
              chambre.getNom(),
              chambre.getEtage(),
              chambre.getStatut(),
              chambre.getPrix(),
              id_reservation
        );
    }
    public Chambre toEntity(ChambreDTO chambreDTO){
        Reservation reservation=(chambreDTO.getId_reservation()!=null)
                ?reservationRepository.findById(chambreDTO.getId())
                .orElseThrow(()->new RuntimeException("Reservation non trouve")):null;

        return new Chambre(
                chambreDTO.getId(),
                chambreDTO.getNom(),
                chambreDTO.getEtage(),
                chambreDTO.getPrix(),
                chambreDTO.getStatut(),
                reservation
        );
    }
}
