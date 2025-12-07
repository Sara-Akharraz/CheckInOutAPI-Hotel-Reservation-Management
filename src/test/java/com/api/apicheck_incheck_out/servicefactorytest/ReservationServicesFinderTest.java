package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ReservationServicesFinderTest {
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServicesFinder finder;
    @Test
    void testFindReservationById(){
        Reservation reservation=new Reservation();
        reservation.setId(1L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result=finder.findReservationById(1L);
        assertNotNull(result);
        assertEquals(1L,result.getId());
    }
    @Test
    void testFindReservationById_ReservationNotFoundException() {

        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> {
            finder.findReservationById(2L);
        });
    }
    @Test
    void testExtractExistingServiceIds() {

        Services service1 = new Services();
        service1.setId(10L);

        Services service2 = new Services();
        service2.setId(20L);

        ReservationServices rs1 = new ReservationServices();
        rs1.setService(service1);

        ReservationServices rs2 = new ReservationServices();
        rs2.setService(service2);


        Reservation reservation = new Reservation();
        reservation.setServiceList(List.of(rs1, rs2));

        Set<Long> result = finder.extractExistingServiceIds(reservation);

        assertEquals(Set.of(10L, 20L), result);
        assertTrue(result.contains(10L));
        assertTrue(result.contains(20L));
    }
    @Test
    void testExtractExistingServiceIds_EmptyList() {

        Reservation reservation = new Reservation();
        reservation.setServiceList(List.of());

        Set<Long> result = finder.extractExistingServiceIds(reservation);

        assertTrue(result.isEmpty());
    }
}
