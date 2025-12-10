package com.api.apicheck_incheck_out.service.factory;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ChambreReservationFinder {

    private final ChambreReservationRepository chambreReservationRepository;

    public ChambreReservationFinder(ChambreReservationRepository chambreReservationRepository) {
        this.chambreReservationRepository = chambreReservationRepository;
    }

    public ChambreReservation findChambreReservation(Long idReservation,Long idChambre){
        return chambreReservationRepository.findByReservation_IdAndChambre_Id(idReservation,idChambre)
                .orElseThrow(()->new ChambreReservationNotFoundException("ChambreReservation non trouvée pour la réservation ID : " + idReservation + " et chambre ID : " + idChambre));
    }
    public List<ChambreReservation> findChambreReservationByReservationId(Long idReservation){
        List<ChambreReservation> chambreReservations=chambreReservationRepository.findByReservation_Id(idReservation);

        if(chambreReservations.isEmpty()){
            throw new ChambreReservationNotFoundException("Aucune ChambreReservation trouvée pour la réservation ID : " + idReservation);
        }
        return chambreReservations;
    }
    public List<ChambreReservation> findChambreReservationsByReservation(Reservation res){
        return chambreReservationRepository.findByReservation(res);
    }



}
