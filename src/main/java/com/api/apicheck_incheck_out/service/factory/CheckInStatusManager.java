package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import org.springframework.stereotype.Component;

@Component
public class CheckInStatusManager {
    private final CheckInRepository checkInRepository;

    public CheckInStatusManager(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }
    public void updateStatus(CheckIn checkIn, CheckInStatus status){
        checkIn.setStatus(status);
        checkInRepository.save(checkIn);
    }
}
