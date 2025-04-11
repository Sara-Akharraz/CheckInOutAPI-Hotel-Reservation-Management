package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Entity.DocumentScan;
import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.CheckInStatus;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;

public interface CheckInService {
    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc);
    public DocumentScan getDocumentByCheckin(Long id);
    public Boolean validerCheckIn(Reservation reservation, PaiementMethod method);
    public Check_In getCheckInByReservation(Long Id_reservation);
    public CheckInStatus getStatusCheckIn(Long Id_reservation);
}
