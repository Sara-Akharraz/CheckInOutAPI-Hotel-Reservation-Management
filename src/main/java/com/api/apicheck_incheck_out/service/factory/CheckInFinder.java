package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.CheckInNotFoundException;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class CheckInFinder {
    private final CheckInRepository checkInRepository;
    private final ReservationServicesFinder resfinder;
    private final ReservationRepository reservationRepository;

    public CheckInFinder(CheckInRepository checkInRepository, ReservationServicesFinder resfinder, ReservationRepository reservationRepository) {
        this.checkInRepository = checkInRepository;
        this.resfinder = resfinder;
        this.reservationRepository = reservationRepository;
    }
    public CheckIn findByReservation(Reservation reservation){
        return checkInRepository.findByReservation(reservation).orElseThrow(()->new CheckInNotFoundException("Aucun check-in trouvé pour cette réservation."));
    }
    public CheckIn findById(Long idCheckin){
        return checkInRepository.findById(idCheckin)
                .orElseThrow(() -> new CheckInNotFoundException("check_in non effectué!"));
    }
    public CheckIn findByReservationId(Long idReservation){
        Reservation reservation=resfinder.findReservationById(idReservation);
        return checkInRepository.findByReservation(reservation).orElse(null);
    }
    public List<CheckIn> findCheckinsForToday(LocalDate today){
        List<Reservation> reservations = reservationRepository.findByDateDebut(today);
        return reservations.stream()
                .map(Reservation::getCheckIn)
                .filter(Objects::nonNull)
                .toList();
    }
}
