package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CheckInFactory {
    private final CheckInRepository checkInRepository;

    public CheckInFactory(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    public CheckIn createCheckIn(Reservation reservation, DocumentScan documentScan, CheckInStatus status) {
        CheckIn checkIn = new CheckIn();
        checkIn.setDateCheckIn(LocalDate.now());
        checkIn.setReservation(reservation);
        checkIn.setDocumentScan(documentScan);
        checkIn.setStatus(status);
        documentScan.setCheckIn(checkIn);

        return checkInRepository.save(checkIn);
    }
}
