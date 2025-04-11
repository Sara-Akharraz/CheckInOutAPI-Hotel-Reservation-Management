package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.ReservationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final PMSService pmsService;
    private final ReservationMapper reservationMapper;
    private final ChambreRepository chambreRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, PMSService pmsService, ReservationMapper reservationMapper, ChambreRepository chambreRepository){
        this.reservationRepository=reservationRepository;
        this.pmsService = pmsService;
        this.reservationMapper = reservationMapper;
        this.chambreRepository = chambreRepository;

    }
    @Override
    public Reservation addReservation(Reservation reservation) {

        List<Long> chambreIds = reservation.getChambreList().stream()
                .map(Chambre::getId)
                .collect(Collectors.toList());

        // Vérification de l'existence d'une réservation avec les mêmes chambres et dates
        List<Reservation> existingReservations = reservationRepository.findExistingReservation(
                reservation.getUser().getId(),
                chambreIds,
                reservation.getDate_debut(),
                reservation.getDate_fin()
        );

        if (!existingReservations.isEmpty()) {
            throw new EntityExistsException("Une réservation existe déjà pour ce Client avec les mêmes chambres et dans les mêmes dates.");
        }


        for (Long chambreId : chambreIds) {
            Chambre chambreEntity = chambreRepository.findById(chambreId)
                    .orElseThrow(() -> new RuntimeException("Chambre non trouvée dans la base de données : " + chambreId));


            if (chambreEntity.getStatut() != ChambreStatut.DISPONIBLE) {
                throw new RuntimeException("La chambre " + chambreId + " est déjà réservée");
            }

            chambreEntity.setStatut(ChambreStatut.RESERVED);
            chambreEntity.setReservation(reservation);


            chambreRepository.save(chambreEntity);
        }


        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        Optional<Reservation> prevReservation=reservationRepository.findById(id);
        if(prevReservation.isPresent()){
            Reservation reservation=prevReservation.get();
            reservation.setStatus(status);
            if(status==ReservationStatus.Confirmee){
                List<Long> chambreIds = reservation.getChambreList().stream()
                        .map(Chambre::getId)
                        .collect(Collectors.toList());

                for (Long chambreId : chambreIds) {
                    Chambre chambreEntity = chambreRepository.findById(chambreId)
                            .orElseThrow(() -> new RuntimeException("Chambre non trouvée dans la base de données : " + chambreId));


                    chambreEntity.setStatut(ChambreStatut.OCCUPEE);
                    chambreEntity.setReservation(reservation);


                    chambreRepository.save(chambreEntity);
                }
            }
            return reservationRepository.save(reservation);
        }
        else{
            throw new RuntimeException("Reservation non trouvée pour l'id :" +id);
        }

    }

    @Override
    public void deleteReservation(Long id) {
        if(reservationRepository.existsById(id)){
            reservationRepository.deleteById(id);
        }else{
            throw new RuntimeException("Reservation non trouvée pour l'id :" +id);
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(()->new RuntimeException("Reservation non trouvée pour l'id :"+ id));
    }

    @Override
    public Reservation addReservationPMS(Long id) {
        ReservationDTO pmsReservationDTO=pmsService.getDemandeReservationById(id);
        if(pmsReservationDTO == null){
            throw new EntityNotFoundException("Reservation introuvable dans le pms");

        }
        if(reservationRepository.existsById(pmsReservationDTO.getId())){
            throw new EntityExistsException("Reservation déja existante en base de donnée");
        }

        Reservation reservation = reservationMapper.toEntity(pmsReservationDTO);
        reservation.setStatus(ReservationStatus.En_Attente);
        reservation=reservationRepository.save(reservation);

        List<Chambre> chambresDisponibles = chambreRepository.findAll();

        for (Long chambreId : pmsReservationDTO.getChambreList()) {

            Chambre chambreEntity = chambreRepository.findById(chambreId)
                    .orElseThrow(() -> new RuntimeException("Chambre non trouvée dans la base de données : " + chambreId));

            if (chambreEntity.getStatut() != ChambreStatut.DISPONIBLE) {
                throw new RuntimeException("La chambre " + chambreId + " est déjà réservée");
            }

            chambreEntity.setStatut(ChambreStatut.RESERVED);
            chambreEntity.setReservation(reservation);

            chambreRepository.save(chambreEntity);
        }

        return reservationRepository.save(reservation);
    }
}
