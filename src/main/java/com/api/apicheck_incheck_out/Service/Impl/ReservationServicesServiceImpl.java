package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.ReservationServices;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.Service.ReservationServicesService;
import com.stripe.param.SubscriptionScheduleUpdateParams;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReservationServicesServiceImpl implements ReservationServicesService {
    private final ReservationServiceRepository reservationServiceRepository;

    public ReservationServicesServiceImpl(ReservationServiceRepository reservationServiceRepository) {
        this.reservationServiceRepository = reservationServiceRepository;
    }

    @Override
    public List<ReservationServices> getAllServicesByReservation(Long id_reservation) {
        return reservationServiceRepository.findByReservationId(id_reservation);
    }

    @Override
    public List<ReservationServices> getServicesByPhase(Long id_reservation, PhaseAjoutService phase) {
        return reservationServiceRepository.findByReservationAndPhase(
                id_reservation,
                phase
        );
    }
}
