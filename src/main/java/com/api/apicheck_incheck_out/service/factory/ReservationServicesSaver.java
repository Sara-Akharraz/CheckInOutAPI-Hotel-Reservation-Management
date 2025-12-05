package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationServicesSaver {
    private final ReservationRepository reservationRepository;
    private final ReservationServiceRepository reservationServiceRepository;

    public ReservationServicesSaver(ReservationRepository reservationRepository, ReservationServiceRepository reservationServiceRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationServiceRepository = reservationServiceRepository;
    }

    public void saveNewServicesIfPresent(Reservation res, List<ReservationServices> newReservationServices){
        if(newReservationServices.isEmpty()){
            return;
        }
        reservationServiceRepository.saveAll(newReservationServices);


        res.getServiceList().addAll(newReservationServices);
        reservationRepository.save(res);

    }
}
