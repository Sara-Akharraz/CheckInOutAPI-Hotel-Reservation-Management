package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.dto.*;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;

import com.api.apicheck_incheck_out.repository.*;
import com.api.apicheck_incheck_out.service.ReservationService;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;
    private final ChambreRepository chambreRepository;

    private final ChambreReservationRepository chambreReservationRepository;
    private final ChambreMapper chambreMapper;
    private final CheckInRepository checkInRepository;
    private final CheckOutRepository checkOutRepository;


    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationMapper reservationMapper, ChambreRepository chambreRepository,ChambreReservationRepository chambreReservationRepository, ChambreMapper chambreMapper, CheckInRepository checkInRepository, CheckOutRepository checkOutRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.chambreRepository = chambreRepository;
        this.chambreReservationRepository = chambreReservationRepository;
        this.chambreMapper = chambreMapper;
        this.checkInRepository = checkInRepository;
        this.checkOutRepository = checkOutRepository;
    }

    @Override
    public Reservation addReservation(Reservation reservation, List<Long> chambreIds) {


        // Vérification de l'existence d'une réservation avec les mêmes chambres et dates
        List<ChambreReservation> existingChambreReservations = chambreReservationRepository.findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(
                chambreIds, reservation.getDateDebut(), reservation.getDateFin());

        if (!existingChambreReservations.isEmpty()) {
            throw new EntityExistsException("Une réservation existe déjà pour ces chambres avec les mêmes dates.");
        }

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

    @Override
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        Optional<Reservation> prevReservation = reservationRepository.findById(id);
        if (prevReservation.isPresent()) {
            Reservation reservation = prevReservation.get();
            reservation.setStatus(status);
            if (status == ReservationStatus.CONFIRMEE) {
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
            return reservationRepository.save(reservation);
        } else {
            throw new ReservationNotFoundException("Reservation non trouvée pour l'id :" + id);
        }

    }

    @Override
    public void deleteReservation(Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
        } else {
            throw new ReservationNotFoundException("Reservation non trouvée pour l'id :" + id);
        }
    }


    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation non trouvée pour l'id :" + id));
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        log.debug("Réservations trouvées : " + reservations);  // Debug
        return reservations;
    }

    @Override
    public List<Reservation> searchReservations(String search, LocalDate dateDebut, LocalDate dateFin, ReservationStatus status) {
        List<Reservation> results = reservationRepository.findAll();


        if (search != null && !search.isBlank()) {
            String lowerSearch = search.toLowerCase();

            results = results.stream()
                    .filter(r ->
                            r.getId().toString().equalsIgnoreCase(lowerSearch) ||


                                    // nom et prénom utilisateur
                                    (r.getUser() != null && (
                                            (r.getUser().getNom() != null && r.getUser().getNom().toLowerCase().contains(lowerSearch)) ||
                                                    (r.getUser().getPrenom() != null && r.getUser().getPrenom().toLowerCase().contains(lowerSearch))
                                    ))
                    )
                    .toList();
        }

        if (dateDebut != null) {
            results = results.stream()
                    .filter(r -> !r.getDateDebut().isBefore(dateDebut))
                    .toList();
        }

        if (dateFin != null) {
            results = results.stream()
                    .filter(r -> !r.getDateFin().isAfter(dateFin))
                    .toList();
        }

        if (status != null) {
            results = results.stream()
                    .filter(r -> r.getStatus() == status)
                    .toList();
        }

        return results;
    }


    public DetailReservationRequestDTO getReservationDetail(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Réservation introuvable avec l'id : " + reservationId));


        ReservationDTO reservationDTO = reservationMapper.toDTO(reservation);


        List<Chambre> chambres = reservation.getChambreReservations().stream()
                .map(cr -> cr.getChambre())
                .toList();

        List<ChambreDTO> chambreDTOs = chambres.stream()
                .map(chambreMapper::toDTO)
                .toList();


        List<ReservationServiceRequestDTO> serviceDTOs = reservation.getServiceList().stream().map(service ->
                new ReservationServiceRequestDTO(
                        reservation.getId(),
                        service.getId(),
                        service.getService().getNom(),
                        service.getService().getDescription(),
                        service.getService().getPrix(),
                        service.getPaiementStatus()
                )
        ).toList();


        return new DetailReservationRequestDTO(reservationDTO,
                serviceDTOs,
                chambreDTOs,
                reservation.getUser().getNom(),
                reservation.getUser().getPrenom(),
                reservation.getUser().getCin(),
                reservation.getUser().getTelephone());
    }
    @Override
    public boolean existsById(Long id) {
        return reservationRepository.existsById(id);
    }

    @Override
    public User findUserByReservation(Long idReservation){
        Reservation reservation=reservationRepository.getById(idReservation);
        return reservation.getUser();
    }

    @Override
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("reservations", reservationRepository.count());
        stats.put("checkins", checkInRepository.count());
        stats.put("checkouts", checkOutRepository.count());
        return stats;
    }

}



