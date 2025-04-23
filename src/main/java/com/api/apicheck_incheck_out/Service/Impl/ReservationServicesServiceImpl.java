package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.ReservationServices;
import com.api.apicheck_incheck_out.Entity.Services;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.Repository.ServiceRepository;
import com.api.apicheck_incheck_out.Service.ReservationServicesService;
import com.stripe.param.SubscriptionScheduleUpdateParams;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ReservationServicesServiceImpl implements ReservationServicesService {
    private final ReservationServiceRepository reservationServiceRepository;
    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;
    public ReservationServicesServiceImpl(ReservationServiceRepository reservationServiceRepository, ReservationRepository reservationRepository, ServiceRepository serviceRepository) {
        this.reservationServiceRepository = reservationServiceRepository;
        this.reservationRepository = reservationRepository;
        this.serviceRepository = serviceRepository;
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
    @Override
    public List<ReservationServices> addResService(Long id_reservation,List<Long> serviceIds){
        Reservation reservation= reservationRepository.findById(id_reservation).orElseThrow(
                ()->new RuntimeException("Reservation non trouvée avec l'id :" +id_reservation)
        );
        List<ReservationServices> addedServices = new ArrayList<>();

        for (Long serviceId : serviceIds) {
            Services service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found: " + serviceId));

            ReservationServices resService = new ReservationServices();
            resService.setReservation(reservation);
            resService.setService(service);
            resService.setPhaseAjoutService(PhaseAjoutService.check_in);
            resService.setPaiementStatus(PaiementStatus.en_attente);

            reservationServiceRepository.save(resService);
            addedServices.add(resService);
        }

        return addedServices;

    }
}
