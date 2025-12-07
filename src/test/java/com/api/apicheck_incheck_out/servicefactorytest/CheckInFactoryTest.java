package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import com.api.apicheck_incheck_out.service.factory.CheckInFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CheckInFactoryTest {
    @Mock
    private CheckInRepository checkInRepository;
    @InjectMocks
    private CheckInFactory factory;

    @Test
    void testCreateCheckIn() {

        Reservation reservation = new Reservation();
        DocumentScan documentScan = mock(DocumentScan.class);

        CheckIn savedCheckIn = new CheckIn();
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(savedCheckIn);

        CheckIn result = factory.createCheckIn(
                reservation,
                documentScan,
                CheckInStatus.EN_ATTENTE
        );

        assertNotNull(result);

        ArgumentCaptor<CheckIn> captor = ArgumentCaptor.forClass(CheckIn.class);
        verify(checkInRepository).save(captor.capture());

        CheckIn checkInCaptured = captor.getValue();

        assertEquals(reservation, checkInCaptured.getReservation());
        assertEquals(CheckInStatus.EN_ATTENTE, checkInCaptured.getStatus());
        assertEquals(LocalDate.now(), checkInCaptured.getDateCheckIn());
        assertEquals(documentScan, checkInCaptured.getDocumentScan());

        verify(documentScan).setCheckIn(checkInCaptured);
    }
}
