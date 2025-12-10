package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.service.ChambreReservationService;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationFilter;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationFinder;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationStatusManager;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChambreReservationServiceImpl implements ChambreReservationService {

    private final ChambreReservationRepository chambreReservationRepository;
    private final ChambreReservationStatusManager manager;
    private final ChambreReservationFinder finder;
    private final ChambreReservationFilter filter;

    public ChambreReservationServiceImpl(ChambreReservationRepository chambreReservationRepository, ChambreReservationStatusManager manager, ChambreReservationFinder finder, ChambreReservationFilter filter) {
        this.chambreReservationRepository = chambreReservationRepository;
        this.manager = manager;
        this.finder = finder;
        this.filter = filter;
    }

    @Override
    public ChambreStatut getChambreStatut(Long reservationId, Long chambreId) {
        ChambreReservation chambreReservation =finder.findChambreReservation(reservationId,chambreId);
        return chambreReservation.getStatut();}

    @Override
    public List<Chambre> getChambresByReservation(Long id) {

        List<ChambreReservation> chambreReservations=finder.findChambreReservationByReservationId(id);
        return chambreReservations.stream().map(ChambreReservation::getChambre).toList();
    }

    @Override
    public List<Chambre> getChambresDisponibles() {
        List<ChambreReservation> chambreReservations=chambreReservationRepository.findByStatut(ChambreStatut.DISPONIBLE);
        return chambreReservations.stream().map(ChambreReservation::getChambre).toList();
    }

    @Override
    public void setChambreOccupee(Long idReservation) {
        manager.setChambreOccupee(idReservation);
    }

    @Override
    public void setChambreDisponible(Long idReservation) {
        manager.setChambreDisponible(idReservation);

    }

    @Override
    public void setChambreReserved(Long idChambre, Long idReservation) {
        manager.setChambreReserved(idChambre,idReservation);
    }
    @Override
    public List<Chambre> findChambresDisponibles(String dateDebut, String dateFin, Integer capacite, ChambreType type, String etage) {
        return filter.findChambresDisponibles(dateDebut, dateFin, capacite, type, etage);
    }

}
