package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.CheckInDTO;
import com.api.apicheck_incheck_out.Entity.Check_In;
import org.springframework.stereotype.Component;

@Component
public class CheckInMapper {

    public static CheckInDTO toDTO(Check_In checkIn) {
        if (checkIn == null) {
            return null;
        }
        CheckInDTO checkIndto = new CheckInDTO();
        checkIndto.setId(checkIn.getId());
        checkIndto.setDateCheckIn(checkIn.getDateCheckIn());
        checkIndto.setStatus(checkIn.getStatus());
        checkIndto.setId_reservation(checkIn.getReservation() != null ? checkIn.getReservation().getId() : null);
        checkIndto.setId_documentScan(checkIn.getDocumentScan() != null ? checkIn.getDocumentScan().getId() : null);

        return checkIndto;
    }

}
