package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
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
    public List<ReservationServices> getAllServicesByReservation(Long idReservation) {
        return reservationServiceRepository.findByReservationId(idReservation);
    }

    @Override
    public List<ReservationServices> getServicesByPhase(Long idReservation, PhaseAjoutService phase) {
        return reservationServiceRepository.findByReservationAndPhase(
                idReservation,
                phase
        );
    }
    @Override
    public List<ReservationServices> addResService(Long idReservation,List<Long> serviceIds){
        Reservation reservation= reservationRepository.findById(idReservation).orElseThrow(
                ()->new RuntimeException("Reservation non trouvée avec l'id :" +idReservation)
        );
        List<ReservationServices> addedServices = new ArrayList<>();

        for (Long serviceId : serviceIds) {
            Services service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found: " + serviceId));

            ReservationServices resService = new ReservationServices();
            resService.setReservation(reservation);
            resService.setService(service);
            resService.setPhaseAjoutService(PhaseAjoutService.CHECK_IN);
            resService.setPaiementStatus(PaiementStatus.EN_ATTENTE);

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
                reservationService.setPaiementStatus(PaiementStatus.EN_ATTENTE);
                reservationService.setPhaseAjoutService(PhaseAjoutService.SEJOUR);

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
    public List<Services> getAvailableServices(Long idReservation) {
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvée avec l'id :" + idReservation));

        List<Long> servicesIds = reservation.getServiceList()
                .stream()
                .map(rs -> rs.getService().getId())
                .toList();

        List<Services> allServices = serviceRepository.findAll();

        return allServices.stream()
                .filter(service -> !servicesIds.contains(service.getId()))
                .toList();
    }

    @Override
    public List<Services> getRsrvServicesSejourUnpaid(Long id) {
        return reservationServiceRepository.getRsrvServicesSejourUnpaid(id).stream()
                .map(ReservationServices::getService)
                .toList();
    }

}
