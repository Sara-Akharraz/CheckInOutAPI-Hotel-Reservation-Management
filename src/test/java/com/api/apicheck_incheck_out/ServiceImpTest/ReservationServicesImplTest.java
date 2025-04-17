package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.ReservationServices;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.Service.Impl.ReservationServicesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServicesImplTest {
    @Mock
    private ReservationServiceRepository reservationServiceRepository;

    @InjectMocks
    private ReservationServicesServiceImpl reservationService;

    private ReservationServices reservationServices;

    @BeforeEach
    void setup(){
        Reservation reservation=new Reservation();
        reservation.setId(1L);

        reservationServices=new ReservationServices();
        reservationServices.setId(1L);
        reservationServices.setReservation(reservation);
        reservationServices.setPhaseAjoutService(PhaseAjoutService.check_in);
    }
    @Test
    void TestgetAllServicesByReservation(){
        List<ReservationServices> servicesList=Arrays.asList(reservationServices);
        when(reservationServiceRepository.findByReservationId(1L)).thenReturn(servicesList);

        List<ReservationServices> result=reservationService.getAllServicesByReservation(1L);

        assertEquals(1,result.size());
        verify(reservationServiceRepository,times(1)).findByReservationId(1L);
    }
    @Test
    void TestgetServicesByPhase(){
        PhaseAjoutService phase=PhaseAjoutService.check_in;
        List<ReservationServices> serviceList= Arrays.asList(reservationServices);
        when(reservationServiceRepository.findByReservationAndPhase(1L,phase)).thenReturn(serviceList);

        List<ReservationServices> result=reservationService.getServicesByPhase(1L, phase);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reservationServices, result.get(0));

        verify(reservationServiceRepository, times(1)).findByReservationAndPhase(1L, phase);

    }

}
