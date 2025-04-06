package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.PMSMock.Service.PMSService;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.ReservationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final PMSService pmsService;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, PMSService pmsService, ReservationMapper reservationMapper){
        this.reservationRepository=reservationRepository;
        this.pmsService = pmsService;
        this.reservationMapper = reservationMapper;
    }
    @Override
    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation updateReservationStatus(Long id, ReservationStatus status) {
        Optional<Reservation> prevReservation=reservationRepository.findById(id);
        if(prevReservation.isPresent()){
            Reservation reservation=prevReservation.get();
            reservation.setStatus(status);
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
        return reservationRepository.save(reservation);
    }
}
