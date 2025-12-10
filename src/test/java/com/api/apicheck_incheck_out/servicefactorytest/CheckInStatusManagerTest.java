package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import com.api.apicheck_incheck_out.service.factory.CheckInStatusManager;
import org.hibernate.annotations.Check;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
 class CheckInStatusManagerTest {
    @Mock
    private CheckInRepository checkInRepository;
    @InjectMocks
    private CheckInStatusManager manager;

    @Test
    void testUpdateStatus() {
        CheckIn checkIn = new CheckIn();
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);

        manager.updateStatus(checkIn, CheckInStatus.VALIDE);

        assert(checkIn.getStatus() == CheckInStatus.VALIDE);

        verify(checkInRepository, times(1)).save(checkIn);
    }
}
