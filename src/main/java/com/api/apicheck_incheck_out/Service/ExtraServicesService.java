package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.ExtraService;

import java.util.List;

public interface ExtraServicesService {
    public double calculateTotalExtraPrice(Long id);
    public List<ExtraService> getExtrasOfReservation(Long id);

}
