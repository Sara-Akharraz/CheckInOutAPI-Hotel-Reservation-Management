package com.api.apicheck_incheck_out.mapper;

import com.api.apicheck_incheck_out.dto.CheckInDTO;
import com.api.apicheck_incheck_out.entity.CheckIn;
import org.springframework.stereotype.Component;

@Component
public class CheckInMapper {

    public  CheckInDTO toDTO(CheckIn checkIn) {
        if (checkIn == null) {
            return null;
        }
        CheckInDTO checkIndto = new CheckInDTO();
        checkIndto.setId(checkIn.getId());
        checkIndto.setDateCheckIn(checkIn.getDateCheckIn());
        checkIndto.setStatus(checkIn.getStatus());
        checkIndto.setIdReservation(checkIn.getReservation() != null ? checkIn.getReservation().getId() : null);
        checkIndto.setIdDocumentScan(checkIn.getDocumentScan() != null ? checkIn.getDocumentScan().getId() : null);

        return checkIndto;
    }

}
