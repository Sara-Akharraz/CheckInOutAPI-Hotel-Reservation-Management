package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.ChambreReservation;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ChambreType;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.ChambreReservationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChambreReservationServiceImpl implements ChambreReservationService {

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
        return chambreReservations.stream().map(ChambreReservation::getChambre).collect(Collectors.toList());
    }

    @Override
    public List<Chambre> getChambresDisponibles() {
        List<ChambreReservation> chambreReservations=chambreReservationRepository.findByStatut(ChambreStatut.DISPONIBLE);
        return chambreReservations.stream().map(ChambreReservation::getChambre).collect(Collectors.toList());
    }

    @Override
    public void setChambreOccupee(Long id_reservation) {

        Reservation reservation = reservationRepository.findById(id_reservation)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + id_reservation));
        if (reservation.getStatus() != ReservationStatus.Confirmee) {
            throw new RuntimeException("La réservation n'est pas confirmée, statut actuel : " + reservation.getStatus());
        }
        List<ChambreReservation> chambreReservations = chambreReservationRepository.findByReservation_Id(id_reservation);
        if (chambreReservations.isEmpty()) {
            throw new RuntimeException("Aucune ChambreReservation trouvée pour la réservation ID : " + id_reservation);
        }

        for (ChambreReservation chambreReservation : chambreReservations) {

            chambreReservation.setStatut(ChambreStatut.OCCUPEE);
            chambreReservation.setReservation(reservation);

            chambreReservationRepository.save(chambreReservation);
        }

    }

    @Override
    public void setChambreDisponible(Long id_reservation) {
        Reservation reservation = reservationRepository.findById(id_reservation)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + id_reservation));

        if (reservation.getStatus() != ReservationStatus.Confirmee) {
            throw new RuntimeException("La réservation n'est pas confirmée, statut actuel : " + reservation.getStatus());
        }

        List<ChambreReservation> chambreReservations = chambreReservationRepository.findByReservation(reservation);


        for (ChambreReservation chambreReservation : chambreReservations) {

            chambreReservation.setStatut(ChambreStatut.DISPONIBLE);

            chambreReservation.setReservation(null);

            chambreReservationRepository.save(chambreReservation);
        }

        reservation.setStatus(ReservationStatus.Terminee);
        reservationRepository.save(reservation);

    }

    @Override
    public void setChambreReserved(Long id_chambre, Long id_reservation) {
        ChambreReservation chambreReservation = chambreReservationRepository.findByReservation_IdAndChambre_Id(id_reservation, id_chambre)
                .orElseThrow(() -> new RuntimeException("ChambreReservation non trouvée pour la réservation ID : " + id_reservation + " et chambre ID : " + id_chambre));
        Reservation reservation = reservationRepository.findById(id_reservation)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + id_reservation));

        if (reservation.getStatus() == ReservationStatus.En_Attente) {

            chambreReservation.setStatut(ChambreStatut.RESERVED);
            chambreReservation.setReservation(reservation);

            chambreReservationRepository.save(chambreReservation);
        } else {
            throw new RuntimeException("La réservation n'est pas en attente, statut actuel : " + reservation.getStatus());
        }
    }
    @Override
    public List<Chambre> findChambresDisponibles(String dateDebut, String dateFin, Integer capacite, ChambreType type, String etage) {
        LocalDate datedebut = LocalDate.parse(dateDebut);
        LocalDate datefin = LocalDate.parse(dateFin);

        List<Chambre> AllchambresList = chambreRepository.findAll();

        return AllchambresList.stream()
                .filter(chambre -> {
                    if (capacite != null && chambre.getCapacite() < capacite) return false;
                    if (type != null && chambre.getType() != type) return false;
                    if (etage != null && !etage.trim().isEmpty() && !chambre.getEtage().equals(etage)) return false;

                    List<ChambreReservation> chambreReservations = chambreReservationRepository.findByChambre_Id(chambre.getId());
                    for (ChambreReservation chambreReservation : chambreReservations) {
                        Reservation reservation = chambreReservation.getReservation();
                        LocalDate resDebut = reservation.getDate_debut();
                        LocalDate resFin = reservation.getDate_fin();

                        if (!(datefin.isBefore(resDebut) || datedebut.isAfter(resFin))) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

}
