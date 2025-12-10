package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;
import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.service.factory.CheckOutFinder;
import com.api.apicheck_incheck_out.service.impl.CheckOutServiceImpl;
import com.api.apicheck_incheck_out.service.impl.FactureServiceImpl;
import com.api.apicheck_incheck_out.service.impl.UserServiceImpl;
import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import kotlin.collections.ArrayDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckOutFinderTest {

    @InjectMocks
    private CheckOutFinder checkOutFinder;

    @Mock
    private ReservationRepository reservationRepository;


    @Mock
    private CheckOutRepository checkOutRepository;

    @Mock
    NotificationService notificationService;

    @Mock
    private StripeServiceImpl stripeService;

    @Mock
    ReservationServicesService reservationServicesService;

    @Mock
    FactureServiceImpl factureService;
    Facture facture;
    @Mock
    private CheckIn checkIn;

    @Mock
    ReservationServiceRepository reservationServiceRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    CheckOut checkOut;

    @Mock
    Reservation reservation;


    List<CheckOut> checkouts = new ArrayDeque<>();
    List<ReservationServices>  reservationServicesList = new ArrayList<>();

    @BeforeEach()
    void setUp(){
        checkIn = CheckIn.builder()
                .id(1L)
                .status(CheckInStatus.VALIDE)
                .build();
        checkOut = CheckOut.builder()
                .id(1L)
                .dateCheckOut(LocalDate.of(2025, 11, 30))
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .build();

        checkouts.add(checkOut);

        Services service = Services.builder()
                .id(1L)
                .nom("Sport")
                .prix(100)
                .description("Votre espace de sport")
                .build();
        Services service1 = Services.builder()
                .id(2L)
                .nom("Wifi")
                .prix(80)
                .description("Wifi fibre optique")
                .build();
        ReservationServices reservationServices = ReservationServices.builder()
                .id(1L)
                .reservation(reservation)
                .paiementStatus(PaiementStatus.EN_ATTENTE)
                .phaseAjoutService(PhaseAjoutService.SEJOUR)
                .service(service)
                .build();
        ReservationServices reservationServices1 = ReservationServices.builder()
                .id(1L)
                .reservation(reservation)
                .service(service1)
                .build();
        reservationServicesList.add(reservationServices);
        reservationServicesList.add(reservationServices1);
        User user = User.builder()
                .id(1L)
                .nom("Alami")
                .prenom("Ali")
                .cin("AB1234")
                .telephone("0611221122")
                .role(Role.CLIENT)
                .build();
        reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .status(ReservationStatus.CONFIRMEE)
                .dateDebut(LocalDate.of(2025, 11, 25))
                .dateFin(LocalDate.of(2025, 11, 30))
                .checkIn(checkIn)
                .checkOut(checkOut)
                .factureList(new ArrayList<>())
                .serviceList(reservationServicesList)
                .build();
        checkOut.setReservation(reservation);

    }

    @Test
    void getCheckOutTest(){
        when(checkOutRepository.findById(1L)).thenReturn(Optional.ofNullable(checkOut));
        CheckOut foundedCheckOut = checkOutFinder.findById(1L);

        assertNotNull(foundedCheckOut);
        assertEquals(checkOut.getId(),foundedCheckOut.getId());
        assertEquals(checkOut.getDateCheckOut(),foundedCheckOut.getDateCheckOut());
    }

    @Test
    void getCheckOutTest_CheckOutNotFoundException(){
        when(checkOutRepository.findById(1L)).thenReturn(Optional.empty());

        CheckOutNotFoundException e = assertThrows(CheckOutNotFoundException.class,
                () -> checkOutFinder.findById(1L));

        assertEquals("Check Out not found with id: " + 1L,e.getMessage());
    }

    @Test
    void getCheckOutByReservationTest(){
        when(reservationRepository.findById(1L)).thenReturn(Optional.ofNullable(reservation));
        when(checkOutRepository.findByReservation(reservation)).thenReturn(Optional.ofNullable(checkOut));

        CheckOut foundedCheckOut = checkOutFinder.findByReservation(reservation.getId());

        assertNotNull(foundedCheckOut);
        assertEquals(checkOut.getId(), foundedCheckOut.getId());
        assertEquals(checkOut.getCheckOutStatut(), foundedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getDateCheckOut(), foundedCheckOut.getDateCheckOut());
    }

    @Test
    void checkoutsForToday(){

        List<Reservation> reservations = new ArrayList<>();

        CheckOut checkOut1 = CheckOut.builder()
                .id(2L)
                .dateCheckOut(LocalDate.of(2025, 11, 30))
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .build();
        Reservation reservation1 = Reservation.builder()
                .id(2L)
                .checkOut(checkOut1)
                .status(ReservationStatus.EN_ATTENTE)
                .dateDebut(LocalDate.of(2025, 11, 25))
                .dateFin(LocalDate.of(2025, 11, 30))
                .checkOut(checkOut1)
                .build();

        reservations.add(reservation);
        reservations.add(reservation1);

        when(reservationRepository.findByDateFin(LocalDate.of(2025,11,25))).thenReturn(reservations);
        List<CheckOut> foundedCheckOutsForToday = checkOutFinder.findCheckoutsForToday(LocalDate.of(2025,11,25));

        assertEquals(2,foundedCheckOutsForToday.size());
        assertEquals(LocalDate.of(2025, 11, 30),foundedCheckOutsForToday.get(0).getDateCheckOut());
        assertEquals(LocalDate.of(2025, 11, 30),foundedCheckOutsForToday.get(1).getDateCheckOut());
        assertEquals(checkOut.getId(),foundedCheckOutsForToday.get(0).getId());
        assertEquals(checkOut1.getId(),foundedCheckOutsForToday.get(1).getId());

    }
}
