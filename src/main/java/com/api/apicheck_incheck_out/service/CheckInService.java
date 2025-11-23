package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;

import java.time.LocalDate;
import java.util.List;

public interface CheckInService {
    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc);
    public DocumentScan getDocumentByCheckin(Long id);

public List<CheckIn> checkinsForToday(LocalDate today);
    Boolean validerCheckIn(Reservation reservation);

    public CheckIn getCheckInByReservation(Long idReservation);
    public CheckInStatus getStatusCheckIn(Long idReservation);
    public void validerCheckinReception(Long idCheckin);
    public void ajoutercheckinReception(Long idReservation,DocumentScan documentScan);
}
