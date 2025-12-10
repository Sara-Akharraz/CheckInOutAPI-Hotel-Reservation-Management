package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class CheckOutFinder {

    CheckOutRepository checkOutRepository;
    ReservationRepository reservationRepository;

    public CheckOut findById(Long id) {
        return checkOutRepository.findById(id)
                .orElseThrow(() -> new CheckOutNotFoundException("Check Out not found with id: " + id));
    }

    public CheckOut findByReservation(Long idReservation) {
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Réservation non trouvée avec l'id : " + idReservation));
        return checkOutRepository.findByReservation(reservation).orElse(null);
    }

    public List<CheckOut> findCheckoutsForToday(LocalDate today) {
        return reservationRepository.findByDateFin(today).stream()
                .map(Reservation::getCheckOut)
                .filter(Objects::nonNull)
                .toList();
    }
}
