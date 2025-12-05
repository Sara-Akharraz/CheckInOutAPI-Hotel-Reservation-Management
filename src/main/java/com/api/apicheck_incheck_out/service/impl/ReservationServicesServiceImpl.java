package com.api.apicheck_incheck_out.service.impl;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesCreator;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesSaver;
import org.springframework.stereotype.Service;


import java.util.List;



@Service
public class ReservationServicesServiceImpl implements ReservationServicesService {
    private final ReservationServiceRepository reservationServiceRepository;
    private final ServiceRepository serviceRepository;
    private final ReservationServicesFinder finder;
    private final ReservationServicesCreator creator;
    private final ReservationServicesSaver saver;
    public ReservationServicesServiceImpl(ReservationServiceRepository reservationServiceRepository,ServiceRepository serviceRepository, ReservationServicesFinder finder, ReservationServicesCreator creator, ReservationServicesSaver saver) {
        this.reservationServiceRepository = reservationServiceRepository;
        this.serviceRepository = serviceRepository;
        this.finder = finder;
        this.creator = creator;
        this.saver = saver;

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
        Reservation reservation=finder.findReservationById(idReservation);
        List<ReservationServices> addedServices = serviceIds.stream()
                        .map(serviceId->creator.createReservationService(reservation,serviceId,PhaseAjoutService.CHECK_IN))
                        .toList();

        reservationServiceRepository.saveAll(addedServices);
        return addedServices;

    }

    @Override
    public void addSejourServicesToReservation(Long idReservation,List<Long> serviceIds){
        Reservation reservation=finder.findReservationById(idReservation);

        List<ReservationServices> newReservationServices= creator.createNewReservationServices(reservation,serviceIds);

        saver.saveNewServicesIfPresent(reservation,newReservationServices);
    }



    @Override
    public List<Services> getAvailableServices(Long idReservation) {
        Reservation reservation = finder.findReservationById(idReservation);


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
