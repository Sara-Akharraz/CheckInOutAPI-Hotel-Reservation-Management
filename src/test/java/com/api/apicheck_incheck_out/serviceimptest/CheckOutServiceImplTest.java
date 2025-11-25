package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.CheckOut;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.impl.CheckOutServiceImpl;
import com.api.apicheck_incheck_out.service.impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.service.impl.UserServiceImpl;
import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import kotlin.collections.ArrayDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckOutServiceImplTest {

    @InjectMocks
    private CheckOutServiceImpl checkOutService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationServiceImpl reservationService;

    @Mock
    private CheckOutRepository checkOutRepository;

    @Mock
    private StripeServiceImpl stripeService;

    @Mock
    private CheckIn checkIn;

    @Mock
    private UserServiceImpl userService;

    @Mock
    CheckOut checkOut;

    @Mock Reservation reservation;

    List<CheckOut> checkouts = new ArrayDeque<>();


    @BeforeEach()
    void setUp(){
        checkIn = CheckIn.builder()
                .id(1L)
                .status(CheckInStatus.VALIDE)
                .build();
        checkOut = CheckOut.builder()
                .id(1L)
                .dateCheckOut(LocalDate.of(2025, 11, 30))
                .checkOutStatut(CheckOutStatut.CONFIRMEE)
                .build();

        checkouts.add(checkOut);
        reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.CONFIRMEE)
                .dateDebut(LocalDate.of(2025, 11, 25))
                .dateFin(LocalDate.of(2025, 11, 30))
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();
    }

    @Test
    void getCheckOutTest(){
        when(checkOutRepository.findById(1L)).thenReturn(Optional.ofNullable(checkOut));
        CheckOut foundedCheckOut = checkOutService.getCheckOutById(1L);

        assertNotNull(foundedCheckOut);
        assertEquals(checkOut.getId(),foundedCheckOut.getId());
        assertEquals(checkOut.getDateCheckOut(),foundedCheckOut.getDateCheckOut());
    }

    @Test
    void addCheckOutTest(){
        when(checkOutRepository.save(any(CheckOut.class))).thenReturn(checkOut);

        CheckOut addedCheckOut = checkOutService.addCheckOut(checkOut);

        assertNotNull(addedCheckOut);
        assertEquals(checkOut.getId(), addedCheckOut.getId());
        assertEquals(checkOut.getCheckOutStatut(), addedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getDateCheckOut(), addedCheckOut.getDateCheckOut());
    }


    @Test
    void getAllUsersTest(){
        CheckOut checkOut1 = CheckOut.builder()
                .id(2L)
                .dateCheckOut(LocalDate.of(2025, 12, 25))
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .reservation(reservation)
                .build();

        checkouts.add(checkOut1);

        when(checkOutRepository.findAll()).thenReturn(checkouts);

        List<CheckOut> foundedCheckOuts = checkOutService.getAllCheckOuts();

        assertEquals(2,foundedCheckOuts.size());
        assertEquals(checkOut.getId(),foundedCheckOuts.get(0).getId());
        assertEquals(checkOut1.getId(),foundedCheckOuts.get(1).getId());
        assertEquals(checkOut.getDateCheckOut(),foundedCheckOuts.get(0).getDateCheckOut());
        assertEquals(checkOut1.getCheckOutStatut(),foundedCheckOuts.get(1).getCheckOutStatut());
    }

    @Test
    void getCheckOutByReservationTest(){
        when(reservationRepository.findById(1L)).thenReturn(Optional.ofNullable(reservation));
        when(checkOutRepository.findByReservation(reservation)).thenReturn(Optional.ofNullable(checkOut));

        CheckOut foundedCheckOut = checkOutService.getCheckOutByReservation(reservation.getId());

        assertNotNull(foundedCheckOut);
        assertEquals(checkOut.getId(), foundedCheckOut.getId());
        assertEquals(checkOut.getCheckOutStatut(), foundedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getDateCheckOut(), foundedCheckOut.getDateCheckOut());

    }

}
