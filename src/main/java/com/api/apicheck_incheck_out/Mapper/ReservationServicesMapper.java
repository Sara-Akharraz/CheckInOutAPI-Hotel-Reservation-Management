package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.ReservationServicesDTO;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.ReservationServices;
import com.api.apicheck_incheck_out.Entity.Services;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Repository.ServiceRepository;
import com.api.apicheck_incheck_out.Service.ReservationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationServicesMapper {

    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;

    public ReservationServicesMapper(ReservationRepository reservationRepository, ServiceRepository serviceRepository) {
        this.reservationRepository = reservationRepository;
        this.serviceRepository = serviceRepository;
    }

    public ReservationServicesDTO toDTO(ReservationServices reservationServices){
        return new ReservationServicesDTO(
                reservationServices.getId(),
                reservationServices.getReservation().getId(),
                reservationServices.getService().getId(),
                reservationServices.getPhaseAjoutService(),
                reservationServices.getPaiementStatus()
        );
    }

    public ReservationServices toEntity(ReservationServicesDTO reservationServicesDTO){
        Reservation reservation=reservationRepository.findById(reservationServicesDTO.getId_reservation()).orElseThrow(()->new RuntimeException("Reservation non trouvée avec l'id :" +reservationServicesDTO.getId_reservation()));
        Services service=serviceRepository.findById(reservationServicesDTO.getId_service()).orElseThrow(()->new RuntimeException("Service non trouvé avec l'id : "+reservationServicesDTO.getId_service()));
        return new ReservationServices(
                reservationServicesDTO.getId(),
                reservation,
                service,
                reservationServicesDTO.getPhaseAjoutService(),
                reservationServicesDTO.getPaiementStatus()
        );

    }
}