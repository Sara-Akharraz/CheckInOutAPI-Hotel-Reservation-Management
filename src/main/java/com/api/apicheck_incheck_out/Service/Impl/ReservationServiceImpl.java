package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DTO.*;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Mapper.ChambreMapper;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;

import com.api.apicheck_incheck_out.Repository.*;
import com.api.apicheck_incheck_out.Service.NotificationService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;
    private final ChambreRepository chambreRepository;

    private final NotificationRepository notificationRepository;

    private final NotificationService notificationService;
    private final ReservationServiceRepository reservationServiceRepository;
    private final ChambreReservationRepository chambreReservationRepository;
    private final ChambreMapper chambreMapper;
    private final CheckInRepository checkInRepository;
    private final CheckOutRepository checkOutRepository;


    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationMapper reservationMapper, ChambreRepository chambreRepository, NotificationRepository notificationRepository, NotificationService notificationService, ReservationServiceRepository reservationServiceRepository, ChambreReservationRepository chambreReservationRepository, ChambreMapper chambreMapper, CheckInRepository checkInRepository, CheckOutRepository checkOutRepository) {
        this.reservationRepository = reservationRepository;

        this.reservationMapper = reservationMapper;
        this.chambreRepository = chambreRepository;

        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.reservationServiceRepository = reservationServiceRepository;
        this.chambreReservationRepository = chambreReservationRepository;
        this.chambreMapper = chambreMapper;
        this.checkInRepository = checkInRepository;
        this.checkOutRepository = checkOutRepository;
    }

    @Override
    public Reservation addReservation(Reservation reservation, List<Long> chambreIds) {

//        List<Long> chambreIds = reservation.getChambreList().stream()
//                .map(Chambre::getId)
//                .collect(Collectors.toList());

        // Vérification de l'existence d'une réservation avec les mêmes chambres et dates
        List<ChambreReservation> existingChambreReservations = chambreReservationRepository.findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(
                chambreIds, reservation.getDate_debut(), reservation.getDate_fin());

        if (!existingChambreReservations.isEmpty()) {
            throw new EntityExistsException("Une réservation existe déjà pour ces chambres avec les mêmes dates.");
        }

        reservation.setStatus(ReservationStatus.En_Attente);
        Reservation savedReservation = reservationRepository.save(reservation);

//        System.out.println("Services reçus côté backend : " + reservation.getServiceList());

        List<String> ListchambreNoms = new ArrayList<>();
        for (Long chambreId : chambreIds) {
            Chambre chambreEntity = chambreRepository.findById(chambreId)
                    .orElseThrow(() -> new RuntimeException("Chambre non trouvée dans la base de données : " + chambreId));

//            List<ChambreReservation> existingChambreReservationsForThisRoom  = chambreReservationRepository.findByChambre_IdAndReservation_StatusAndReservation_IdNot(
//                    chambreId, ReservationStatus.En_Attente, reservation.getId());

//            if (!existingChambreReservationsForThisRoom .isEmpty()) {
//                throw new RuntimeException("La chambre " + chambreId + " est déjà réservée dans une autre réservation en attente.");
//            }

            ChambreReservation chambreReservation = new ChambreReservation();
            chambreReservation.setChambre(chambreEntity);
            chambreReservation.setReservation(savedReservation);
            chambreReservation.setStatut(ChambreStatut.RESERVED);

            chambreReservationRepository.save(chambreReservation);


            ListchambreNoms.add(chambreEntity.getNom());

        }


        return savedReservation;
    }

    @Override
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        Optional<Reservation> prevReservation = reservationRepository.findById(id);
        if (prevReservation.isPresent()) {
            Reservation reservation = prevReservation.get();
            reservation.setStatus(status);
            if (status == ReservationStatus.Confirmee) {
                List<Long> chambreIds = reservation.getChambreReservations().stream()
                        .map(ChambreReservation::getId)
                        .collect(Collectors.toList());

                for (Long chambreId : chambreIds) {
                    ChambreReservation chambreEntity = chambreReservationRepository.findById(chambreId)
                            .orElseThrow(() -> new RuntimeException("Chambre non trouvée dans la base de données : " + chambreId));


                    chambreEntity.setStatut(ChambreStatut.OCCUPEE);
                    chambreEntity.setReservation(reservation);


                    chambreReservationRepository.save(chambreEntity);
                }
            }
            return reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Reservation non trouvée pour l'id :" + id);
        }

    }

    @Override
    public void deleteReservation(Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
        } else {
            throw new RuntimeException("Reservation non trouvée pour l'id :" + id);
        }
    }

//    @Override
//    public List<Reservation> getAllReservations() {
//        return reservationRepository.findAll();
//    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservation non trouvée pour l'id :" + id));
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        System.out.println("Réservations trouvées : " + reservations);  // Debug
        return reservations;
    }

//    @Override
//    public Reservation addReservationPMS(Long id) {
//        ReservationDTO pmsReservationDTO=pmsService.getDemandeReservationById(id);
//        if(pmsReservationDTO == null){
//            throw new EntityNotFoundException("Reservation introuvable dans le pms");
//
//        }
//        if(reservationRepository.existsById(pmsReservationDTO.getId())){
//            throw new EntityExistsException("Reservation déja existante en base de donnée");
//        }
//
//        Reservation reservation = reservationMapper.toEntity(pmsReservationDTO);
//        reservation.setStatus(ReservationStatus.En_Attente);
//        reservation=reservationRepository.save(reservation);
//
//        List<Chambre> chambresDisponibles = chambreRepository.findAll();
//
//        for (Long chambreId : pmsReservationDTO.getChambreList()) {
//
//            Chambre chambreEntity = chambreRepository.findById(chambreId)
//                    .orElseThrow(() -> new RuntimeException("Chambre non trouvée dans la base de données : " + chambreId));
//
//            if (chambreEntity.getStatut() != ChambreStatut.DISPONIBLE) {
//                throw new RuntimeException("La chambre " + chambreId + " est déjà réservée");
//            }
//
//            chambreEntity.setStatut(ChambreStatut.RESERVED);
//            chambreEntity.setReservation(reservation);
//
//            chambreRepository.save(chambreEntity);
//        }
//
//        return reservationRepository.save(reservation);
//    }

    @Override
    public List<Reservation> searchReservations(String search, LocalDate dateDebut, LocalDate dateFin, ReservationStatus status) {
        List<Reservation> results = reservationRepository.findAll();
//
//    if ((search == null || search.isBlank()) && dateDebut == null && dateFin == null) {
//        return results;
//    }

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
                    .collect(Collectors.toList());
        }

        if (dateDebut != null) {
            results = results.stream()
                    .filter(r -> !r.getDate_debut().isBefore(dateDebut))
                    .collect(Collectors.toList());
        }

        if (dateFin != null) {
            results = results.stream()
                    .filter(r -> !r.getDate_fin().isAfter(dateFin))
                    .collect(Collectors.toList());
        }

        if (status != null) {
            results = results.stream()
                    .filter(r -> r.getStatus() == status)
                    .collect(Collectors.toList());
        }

        return results;
    }


    public DetailReservationRequestDTO getReservationDetail(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'ID : " + reservationId));


        ReservationDTO reservationDTO = reservationMapper.toDTO(reservation);


        List<Chambre> chambres = reservation.getChambreReservations().stream()
                .map(cr -> cr.getChambre())
                .collect(Collectors.toList());

        List<ChambreDTO> chambreDTOs = chambres.stream()
                .map(chambreMapper::toDTO)
                .collect(Collectors.toList());


        List<ReservationServiceRequestDTO> serviceDTOs = reservation.getServiceList().stream().map(service ->
                new ReservationServiceRequestDTO(
                        reservation.getId(),
                        service.getId(),
                        service.getService().getNom(),
                        service.getService().getDescription(),
                        service.getService().getPrix(),
                        service.getPaiementStatus()
                )
        ).collect(Collectors.toList());


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
    public User findUserByReservation(Long id_resevation){
        Reservation reservation=reservationRepository.getById(id_resevation);
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



