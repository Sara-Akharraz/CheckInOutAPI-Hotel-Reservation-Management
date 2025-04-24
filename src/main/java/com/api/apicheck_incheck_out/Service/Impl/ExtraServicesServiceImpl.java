package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.ExtraService;
import com.api.apicheck_incheck_out.Service.ExtraServicesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtraServicesServiceImpl implements ExtraServicesService {
    @Override
    public double calculateTotalExtraPrice(Long id) {
        return 0;
    }

    @Override
    public List<ExtraService> getExtrasOfReservation(Long id) {
        return List.of();
    }
}
