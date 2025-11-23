
package com.api.apicheck_incheck_out.mapper;

import com.api.apicheck_incheck_out.dto.CheckOutDTO;
import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.service.ReservationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckOutMapper {


    private final ReservationService reservationService;

    public CheckOutMapper(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public CheckOut toEntity(CheckOutDTO dto){
        Reservation reservation = reservationService.getReservationById(dto.getIdReservation());
        return CheckOut.builder().reservation(reservation)
                .id(dto.getId())
                .checkOutStatut(dto.getCheckOutStatut()).build();
    }
    public CheckOutDTO toDTO(CheckOut entity){
        return CheckOutDTO.builder()
                .id(entity.getId())
                .checkOutStatut(entity.getCheckOutStatut())
                .idReservation(entity.getReservation().getId()).build();
    }

    public List<CheckOutDTO> toDTOList(List<CheckOut> entities) {
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

}