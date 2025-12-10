package com.api.apicheck_incheck_out.servicefactorytest;


import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.factory.CheckOutFinder;
import com.api.apicheck_incheck_out.service.factory.CheckOutStatusManager;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckOutStatusManagerTest {

    @Mock
    CheckOutRepository checkOutRepository;
    @InjectMocks
    CheckOutStatusManager checkOutStatusManager;
    @Mock
    CheckOutFinder checkOutFinder;
    CheckOut checkOut;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ChambreReservationRepository chambreReservationRepository;
    @BeforeEach
    void setUp(){
        checkOut = CheckOut.builder()
                .id(1L)
                .dateCheckOut(LocalDate.of(2025, 11, 30))
                .checkOutStatut(CheckOutStatut.CONFIRMEE)
                .build();

    }
    @Test
    void setCheckOutStatutTest(){
        when(checkOutFinder.findById(1L)).thenReturn(checkOut);
        when(checkOutRepository.save(any(CheckOut.class))).thenReturn(checkOut);

        CheckOut updatedCheckOut = checkOutStatusManager.newStatus(1L, CheckOutStatut.CONFIRMEE);

        assertNotNull(updatedCheckOut);
        assertEquals(CheckOutStatut.CONFIRMEE,updatedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getId(), updatedCheckOut.getId());
    }

    @Test
    void newWaitedCheckOutTest(){
        when(checkOutRepository.save(any(CheckOut.class))).thenReturn(checkOut);

        CheckOut updatedCheckOut = checkOutStatusManager.newWaitedCheckOut(checkOut);

        assertNotNull(updatedCheckOut);
        assertEquals(CheckOutStatut.EN_ATTENTE,updatedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getId(), updatedCheckOut.getId());
    }

    @Test
    void newWaitedCheckOut_CheckOutNullTest(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> checkOutStatusManager.newWaitedCheckOut(null));

        assertEquals("Check_Out object cannot be null", e.getMessage());
    }

    @Test
    void confirmedTest(){
        Reservation reservation = Reservation.builder()
                .id(1L)
                .build();
        checkOut.setReservation(reservation);
        when(checkOutRepository.save(any(CheckOut.class))).thenReturn(checkOut);
        when(checkOutFinder.findById(1L)).thenReturn(checkOut);

        CheckOut updatedCheckOut = checkOutStatusManager.confirmed(1L);

        assertNotNull(updatedCheckOut);
        assertEquals(CheckOutStatut.CONFIRMEE,updatedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getId(), updatedCheckOut.getId());
    }


}
