package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Entity.DocumentScan;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;

public interface CheckInService {

    public Boolean validerScan(Reservation reservation, DocumentScanDTO doc);
    public DocumentScan getDocumentByChekin(Long id);
    public Boolean validerCheckIn(Reservation reservation, PaiementMethod method);
}
