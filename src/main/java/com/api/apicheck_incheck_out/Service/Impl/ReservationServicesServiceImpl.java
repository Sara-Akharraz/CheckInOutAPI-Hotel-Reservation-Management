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
import java.util.stream.Collectors;

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
    @Override
    public void addSejourServicesToReservation(Long idReservation,List<Long> serviceIds){
        Reservation reservation =reservationRepository.findById(idReservation)
                .orElseThrow(()->new RuntimeException("Reservation non trouvée avec l'id : "+idReservation));

        List<ReservationServices> newReservationServices= new ArrayList<>();

        for(Long serviceId :serviceIds){
            boolean alreadyExists =reservation.getServiceList()
                    .stream()
                    .anyMatch(rs->rs.getService().getId().equals(serviceId));
            if(!alreadyExists){
                Services service =serviceRepository.findById(serviceId)
                        .orElseThrow(()->new RuntimeException("Service non trouvée avec l'id :"+serviceId));

                ReservationServices reservationService = new ReservationServices();
                reservationService.setReservation(reservation);
                reservationService.setService(service);
                reservationService.setPaiementStatus(PaiementStatus.en_attente);
                reservationService.setPhaseAjoutService(PhaseAjoutService.sejour);

                newReservationServices.add(reservationService);

            }
            if (!newReservationServices.isEmpty()) {

                reservationServiceRepository.saveAll(newReservationServices);


                reservation.getServiceList().addAll(newReservationServices);
                reservationRepository.save(reservation);
            }
        }
    }
    @Override
    public List<Services> getAvailableServices(Long id_reservation) {
        Reservation reservation = reservationRepository.findById(id_reservation)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvée avec l'id :" + id_reservation));

        List<Long> servicesIds = reservation.getServiceList()
                .stream()
                .map(rs -> rs.getService().getId())
                .collect(Collectors.toList());

        List<Services> allServices = serviceRepository.findAll();

        return allServices.stream()
                .filter(service -> !servicesIds.contains(service.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Services> getRsrvServicesSejourUnpaid(Long id) {
        return reservationServiceRepository.getRsrvServicesSejourUnpaid(id).stream()
                .map(reservationService -> reservationService.getService())
                .collect(Collectors.toList());
    }

}
