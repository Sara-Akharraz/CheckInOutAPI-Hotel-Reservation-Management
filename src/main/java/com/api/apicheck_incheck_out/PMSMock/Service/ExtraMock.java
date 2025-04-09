package com.api.apicheck_incheck_out.PMSMock.Service;

import com.api.apicheck_incheck_out.PMSMock.Model.ExtraModel;

import java.util.List;

public interface ExtraMock {
    public double calculateTotalExtraPrice(Long id_reservation);
    public List<ExtraModel> getExtrasOfReservation(Long id_reservation);
}
