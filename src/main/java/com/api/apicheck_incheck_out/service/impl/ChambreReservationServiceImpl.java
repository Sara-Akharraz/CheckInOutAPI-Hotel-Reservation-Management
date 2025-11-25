package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreReservationNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidReservationStatusException;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.ChambreReservationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ChambreReservationServiceImpl implements ChambreReservationService {

    private static final String RSVNOTFOUND="Réservation non trouvée avec l'id ";

    private final ChambreReservationRepository chambreReservationRepository;
    private final ChambreRepository chambreRepository;
    private final ReservationRepository reservationRepository;

    public ChambreReservationServiceImpl(ChambreReservationRepository chambreReservationRepository, ChambreRepository chambreRepository, ReservationRepository reservationRepository) {
        this.chambreReservationRepository = chambreReservationRepository;
        this.chambreRepository = chambreRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ChambreStatut getChambreStatut(Long reservationId, Long chambreId) {
        ChambreReservation chambreReservation = chambreReservationRepository.findByReservation_IdAndChambre_Id(reservationId, chambreId)
                .orElseThrow(() -> new RuntimeException("ChambreReservation non trouvée pour la réservation ID : " + reservationId + " et chambre ID : " + chambreId));
        return chambreReservation.getStatut();}

    @Override
    public List<Chambre> getChambresByReservation(Long id) {

        List<ChambreReservation> chambreReservations=chambreReservationRepository.findByReservation_Id(id);
        return chambreReservations.stream().map(ChambreReservation::getChambre).toList();
    }

    @Override
    public List<Chambre> getChambresDisponibles() {
        List<ChambreReservation> chambreReservations=chambreReservationRepository.findByStatut(ChambreStatut.DISPONIBLE);
        return chambreReservations.stream().map(ChambreReservation::getChambre).toList();
    }

    @Override
    public void setChambreOccupee(Long idReservation) {

        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ReservationNotFoundException(RSVNOTFOUND + idReservation));
        if (reservation.getStatus() != ReservationStatus.CONFIRMEE) {
            throw new InvalidReservationStatusException("La réservation n'est pas confirmée, statut actuel : " + reservation.getStatus());
        }
        List<ChambreReservation> chambreReservations = chambreReservationRepository.findByReservation_Id(idReservation);
        if (chambreReservations.isEmpty()) {
            throw new ChambreReservationNotFoundException("Aucune ChambreReservation trouvée pour la réservation ID : " + idReservation);
        }

        for (ChambreReservation chambreReservation : chambreReservations) {

            chambreReservation.setStatut(ChambreStatut.OCCUPEE);
            chambreReservation.setReservation(reservation);

            chambreReservationRepository.save(chambreReservation);
        }

    }

    @Override
    public void setChambreDisponible(Long idReservation) {
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ReservationNotFoundException(RSVNOTFOUND + idReservation));

        if (reservation.getStatus() != ReservationStatus.CONFIRMEE) {
            throw new InvalidReservationStatusException("La réservation n'est pas confirmée, statut actuel : " + reservation.getStatus());
        }

        List<ChambreReservation> chambreReservations = chambreReservationRepository.findByReservation(reservation);


        for (ChambreReservation chambreReservation : chambreReservations) {

            chambreReservation.setStatut(ChambreStatut.DISPONIBLE);

            chambreReservation.setReservation(null);

            chambreReservationRepository.save(chambreReservation);
        }

        reservation.setStatus(ReservationStatus.TERMINEE);
        reservationRepository.save(reservation);

    }

    @Override
    public void setChambreReserved(Long idChambre, Long idReservation) {
        ChambreReservation chambreReservation = chambreReservationRepository.findByReservation_IdAndChambre_Id(idReservation, idChambre)
                .orElseThrow(() -> new ChambreReservationNotFoundException("ChambreReservation non trouvée pour la réservation ID : " + idReservation + " et chambre ID : " + idChambre));
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ReservationNotFoundException(RSVNOTFOUND + idReservation));

        if (reservation.getStatus() == ReservationStatus.EN_ATTENTE) {

            chambreReservation.setStatut(ChambreStatut.RESERVED);
            chambreReservation.setReservation(reservation);

            chambreReservationRepository.save(chambreReservation);
        } else {
            throw new InvalidReservationStatusException("La réservation n'est pas en attente, statut actuel : " + reservation.getStatus());
        }
    }
    @Override
    public List<Chambre> findChambresDisponibles(String dateDebut, String dateFin, Integer capacite, ChambreType type, String etage) {
        LocalDate datedebut = LocalDate.parse(dateDebut);
        LocalDate datefin = LocalDate.parse(dateFin);

        List<Chambre> allchambresList = chambreRepository.findAll();

        return allchambresList.stream()
                .filter(chambre -> {
                    if (capacite != null && chambre.getCapacite() < capacite) return false;
                    if (type != null && chambre.getType() != type) return false;
                    if (etage != null && !etage.trim().isEmpty() && !chambre.getEtage().equals(etage)) return false;

                    List<ChambreReservation> chambreReservations = chambreReservationRepository.findByChambre_Id(chambre.getId());
                    for (ChambreReservation chambreReservation : chambreReservations) {
                        Reservation reservation = chambreReservation.getReservation();
                        LocalDate resDebut = reservation.getDateDebut();
                        LocalDate resFin = reservation.getDateFin();

                        if (!(datefin.isBefore(resDebut) || datedebut.isAfter(resFin))) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }

}
