package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.Entity.DocumentScan;
import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.CheckInStatus;

import java.time.LocalDate;
import java.util.List;

public interface CheckInService {
    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc);
    public DocumentScan getDocumentByCheckin(Long id);
//    public Boolean validerCheckIn(Reservation reservation, PaiementMethod method);
public List<Check_In> checkinsForToday(LocalDate today);
    Boolean validerCheckIn(Reservation reservation);

    public Check_In getCheckInByReservation(Long Id_reservation);
    public CheckInStatus getStatusCheckIn(Long Id_reservation);
    public void validerCheckinReception(Long id_checkin);
    public void ajoutercheckinReception(Long id_reservation,DocumentScan documentScan);
}
