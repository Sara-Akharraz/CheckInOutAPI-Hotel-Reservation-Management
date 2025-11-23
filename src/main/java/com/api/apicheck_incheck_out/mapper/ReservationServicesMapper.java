package com.api.apicheck_incheck_out.mapper;

import com.api.apicheck_incheck_out.dto.ReservationServicesDTO;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import org.springframework.stereotype.Component;

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
       Reservation reservation=reservationRepository.findById(reservationServicesDTO.getIdReservation()).orElseThrow(()->new RuntimeException("Reservation non trouvée avec l'id :" +reservationServicesDTO.getIdReservation()));
       Services service=serviceRepository.findById(reservationServicesDTO.getIdService()).orElseThrow(()->new RuntimeException("Service non trouvé avec l'id : "+reservationServicesDTO.getIdService()));
        return new ReservationServices(
                reservationServicesDTO.getId(),
                reservation,
                service,
                reservationServicesDTO.getPhaseAjoutService(),
                reservationServicesDTO.getPaiementStatus()
        );

    }
}
