package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesSaver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class ReservationServicesSaverTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationServiceRepository reservationServiceRepository;

    @InjectMocks
    private ReservationServicesSaver saver;

    private Reservation reservation;
    @BeforeEach
    void setUp(){
        reservation=new Reservation();
        reservation.setServiceList(new ArrayList<>());
    }
    @Test
    void testSaveNewServicesIfPresent_EmptyList(){
        saver.saveNewServicesIfPresent(reservation, List.of());

        verify(reservationServiceRepository,never()).saveAll(any());
        verify(reservationRepository,never()).save(any());
        assertTrue(reservation.getServiceList().isEmpty());
    }
    @Test
    void testSaveNewServicesIfPresent(){
        ReservationServices rs=new ReservationServices();
        List<ReservationServices> newRs=List.of(rs);

        saver.saveNewServicesIfPresent(reservation,newRs);

        verify(reservationServiceRepository,times(1)).saveAll(newRs);
        verify(reservationRepository,times(1)).save(reservation);

        assertEquals(1,reservation.getServiceList().size());
        assertTrue(reservation.getServiceList().contains(rs));
    }
}
