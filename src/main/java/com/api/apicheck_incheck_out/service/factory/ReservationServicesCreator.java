package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.exceptionhandling.ServiceNotFoundException;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ReservationServicesCreator {
    private final ServiceRepository serviceRepository;

    private final ReservationServicesFinder reservationServicesFinder;

    public ReservationServicesCreator(ServiceRepository serviceRepository, ReservationServicesFinder reservationServicesFinder) {
        this.serviceRepository = serviceRepository;
        this.reservationServicesFinder = reservationServicesFinder;
    }

    public ReservationServices createReservationService(Reservation reservation, Long serviceId, PhaseAjoutService phase) {
        Services service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(
                        "Service non trouvée avec l'id : " + serviceId
                ));

        ReservationServices reservationService = new ReservationServices();
        reservationService.setReservation(reservation);
        reservationService.setService(service);
        reservationService.setPaiementStatus(PaiementStatus.EN_ATTENTE);
        reservationService.setPhaseAjoutService(phase);

        return reservationService;
    }
    public List<ReservationServices> createNewReservationServices(
            Reservation reservation,
            List<Long> serviceIds
    ) {
        Set<Long> existingServiceIds =reservationServicesFinder.extractExistingServiceIds(reservation);

        return serviceIds.stream()
                .filter(serviceId -> !existingServiceIds.contains(serviceId))
                .map(serviceId -> createReservationService(reservation, serviceId,PhaseAjoutService.SEJOUR))
                .toList();
    }
}
