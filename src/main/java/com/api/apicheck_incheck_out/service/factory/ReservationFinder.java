package com.api.apicheck_incheck_out.service.factory;


import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.dto.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.dto.ReservationDTO;
import com.api.apicheck_incheck_out.dto.ReservationServiceRequestDTO;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.User;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class ReservationFinder {

    ReservationRepository reservationRepository;
    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationNotFoundException("Reservation non trouvée pour l'id :" + id));
    }

    public List<Reservation> findByUser(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        log.debug("Réservations trouvées : " + reservations);  // Debug
        return reservations;
    }


    public List<Reservation> search(String search, LocalDate dateDebut, LocalDate dateFin, ReservationStatus status) {
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

    public List<ReservationServiceRequestDTO> findServices(Reservation reservation){
        return reservation.getServiceList().stream().map(service ->
                new ReservationServiceRequestDTO(
                        reservation.getId(),
                        service.getId(),
                        service.getService().getNom(),
                        service.getService().getDescription(),
                        service.getService().getPrix(),
                        service.getPaiementStatus()
                )
        ).toList();
    }

    public User findUserOfReservation(Long idReservation){
        Reservation reservation=reservationRepository.getById(idReservation);
        return reservation.getUser();
    }

    public DetailReservationRequestDTO details(Reservation reservation, ReservationDTO reservationDTO, List<ReservationServiceRequestDTO> serviceDTOs, List<ChambreDTO> rooms){
        return new DetailReservationRequestDTO(reservationDTO,
                serviceDTOs,
                rooms,
                reservation.getUser().getNom(),
                reservation.getUser().getPrenom(),
                reservation.getUser().getCin(),
                reservation.getUser().getTelephone());
    }

    public void existante(Long id){
        if (!reservationRepository.existsById(id))
            throw new ReservationNotFoundException("Reservation non trouvée pour l'id :" + id);
    }
}
