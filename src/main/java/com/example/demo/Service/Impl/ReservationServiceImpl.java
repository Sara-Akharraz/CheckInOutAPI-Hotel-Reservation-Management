package com.example.demo.Service.Impl;

import com.example.demo.Entity.Reservation;
import com.example.demo.Enums.ReservationStatus;
import com.example.demo.Repository.ReservationRepository;
import com.example.demo.Service.ReservationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    public ReservationServiceImpl(ReservationRepository reservationRepository){
        this.reservationRepository=reservationRepository;
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
}
