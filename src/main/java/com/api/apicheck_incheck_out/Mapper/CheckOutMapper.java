
package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.CheckOutDTO;
import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CheckOutMapper {

    @Autowired
    ReservationService reservationService;

    public Check_Out toEntity(CheckOutDTO dto){
        Reservation reservation = reservationService.getReservationById(dto.getId_reservation());
        return Check_Out.builder().reservation(reservation)
                .id(dto.getId())
                .checkOutStatut(dto.getCheckOutStatut()).build();
    }
    public CheckOutDTO toDTO(Check_Out entity){
        return CheckOutDTO.builder()
                .id(entity.getId())
                .checkOutStatut(entity.getCheckOutStatut())
                .id_reservation(entity.getReservation().getId()).build();
    }

    public List<CheckOutDTO> toDTOList(List<Check_Out> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}