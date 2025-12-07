package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.CheckInNotFoundException;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.factory.CheckInFinder;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class CheckInFinderTest {
    @Mock
    private CheckInRepository checkInRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationServicesFinder resfinder;
    @InjectMocks
    private CheckInFinder finder;
    private Reservation reservation;
    private CheckIn checkIn;

    @BeforeEach()
    void setUp(){
        reservation = new Reservation();
        reservation.setId(1L);

        checkIn = new CheckIn();
        checkIn.setId(10L);
        checkIn.setReservation(reservation);
    }
    @Test
    void testFindByReservation_Success() {
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));

        CheckIn result = finder.findByReservation(reservation);

        assertEquals(checkIn, result);
    }

    @Test
    void testFindByReservation_NotFound() {
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.empty());

        CheckInNotFoundException ex = assertThrows(CheckInNotFoundException.class,
                () -> finder.findByReservation(reservation));

        assertEquals("Aucun check-in trouvé pour cette réservation.", ex.getMessage());
    }

    @Test
    void testFindById_Success() {
        when(checkInRepository.findById(10L)).thenReturn(Optional.of(checkIn));

        CheckIn result = finder.findById(10L);

        assertEquals(checkIn, result);
    }

    @Test
    void testFindById_NotFound() {
        when(checkInRepository.findById(10L)).thenReturn(Optional.empty());

        CheckInNotFoundException ex = assertThrows(CheckInNotFoundException.class,
                () -> finder.findById(10L));

        assertEquals("check_in non effectué!", ex.getMessage());
    }

    @Test
    void testFindByReservationId_Found() {
        when(resfinder.findReservationById(1L)).thenReturn(reservation);
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.of(checkIn));

        CheckIn result = finder.findByReservationId(1L);

        assertEquals(checkIn, result);
    }

    @Test
    void testFindByReservationId_NotFound() {
        when(resfinder.findReservationById(1L)).thenReturn(reservation);
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.empty());

        CheckIn result = finder.findByReservationId(1L);

        assertNull(result);
    }

    @Test
    void testFindCheckinsForToday() {
        LocalDate today = LocalDate.now();
        Reservation r1 = new Reservation();
        r1.setCheckIn(checkIn);

        Reservation r2 = new Reservation();

        when(reservationRepository.findByDateDebut(today)).thenReturn(List.of(r1, r2));

        List<CheckIn> result = finder.findCheckinsForToday(today);

        assertEquals(1, result.size());
        assertEquals(checkIn, result.get(0));
    }
}
