package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import org.springframework.stereotype.Component;


import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReservationServicesFinder {

    private final ReservationRepository reservationRepository;

    public ReservationServicesFinder(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation findReservationById(Long idReservation){
        return reservationRepository.findById(idReservation).orElseThrow(()->new ReservationNotFoundException("Reservation non trouvée avec l'id : " + idReservation));
    }
    public Set<Long> extractExistingServiceIds(Reservation res){
        return res.getServiceList().stream()
                .map(rs->rs.getService().getId())
                .collect(Collectors.toSet());
    }
}
