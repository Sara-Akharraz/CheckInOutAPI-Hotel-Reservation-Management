package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.service.impl.CheckOutServiceImpl;
import com.api.apicheck_incheck_out.service.impl.FactureServiceImpl;
import com.api.apicheck_incheck_out.service.impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.service.impl.UserServiceImpl;
import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import com.stripe.net.StripeRequest;
import kotlin.collections.ArrayDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class CheckOutServiceImplTest {

    @InjectMocks
    private CheckOutServiceImpl checkOutService;

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

    @Mock Reservation reservation;

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

    @Test
    void setCheckOutStatutTest(){
        when(checkOutRepository.findById(1L)).thenReturn(Optional.ofNullable(checkOut));
        when(checkOutRepository.save(any(CheckOut.class))).thenReturn(checkOut);

        CheckOut updatedCheckOut = checkOutService.setCheckOutStatus(1L, CheckOutStatut.CONFIRMEE);

        assertNotNull(updatedCheckOut);
        assertEquals(CheckOutStatut.CONFIRMEE,updatedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getId(), updatedCheckOut.getId());
    }

    @Test
    void getAmount(){
        when(checkOutRepository.findById(1L)).thenReturn(Optional.ofNullable(checkOut));
        when(reservationServicesService.getAllServicesByReservation(1L)).thenReturn(reservationServicesList);

        double calculatedAmount = checkOutService.getAmount(1L);

        Mockito.verify(checkOutRepository).findById(1L);
        Mockito.verify(reservationServicesService).getAllServicesByReservation(1L);
        assertEquals(180, calculatedAmount);
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
         List<CheckOut> foundedCheckOutsForToday = checkOutService.checkoutsForToday(LocalDate.of(2025,11,25));

         assertEquals(2,foundedCheckOutsForToday.size());
         assertEquals(LocalDate.of(2025, 11, 30),foundedCheckOutsForToday.get(0).getDateCheckOut());
         assertEquals(LocalDate.of(2025, 11, 30),foundedCheckOutsForToday.get(1).getDateCheckOut());
         assertEquals(checkOut.getId(),foundedCheckOutsForToday.get(0).getId());
         assertEquals(checkOut1.getId(),foundedCheckOutsForToday.get(1).getId());

    }

    @Test
    void payer(){
        StripeResponse stripeResponse = StripeResponse.builder()
                .status("SUCCESS")
                .message("SUCCESS")
                .sessionId("1111")
                .sessionUrl("session-url")
                .build();

        when(checkOutRepository.findById(1L)).thenReturn(Optional.ofNullable(checkOut));
        when(stripeService.checkoutServices(any(CheckOutRequest.class))).thenReturn(stripeResponse);

        StripeResponse returnedStripeResponse =  checkOutService.payer(1L);

        assertNotNull(returnedStripeResponse);
        assertEquals(stripeResponse.getStatus(), returnedStripeResponse.getStatus());
        assertEquals(stripeResponse.getSessionId(), returnedStripeResponse.getSessionId());
        assertEquals(stripeResponse.getSessionUrl(), returnedStripeResponse.getSessionUrl());
    }


    @Test
    void handlePaymentSuccess(){

        reservationServicesList.get(0).setPaiementStatus(PaiementStatus.PAYE);
        reservationServicesList.get(1).setPaiementStatus(PaiementStatus.PAYE);

        when(checkOutRepository.findById(1L)).thenReturn(Optional.ofNullable(checkOut));
        when(checkOutRepository.save(any(CheckOut.class))).thenReturn(checkOut);
        when(reservationServicesService.getServicesByPhase(1L, PhaseAjoutService.SEJOUR)).thenReturn(reservationServicesList);

        when(userService.getAdmins()).thenReturn(List.of());
        when(userService.getReceptionists()).thenReturn(List.of());

        when(reservationServiceRepository.saveAll(anyList())).thenReturn(reservationServicesList);
         facture = Facture.builder()
                .type(FactureType.CHECK_OUT)
                .status(PaiementStatus.PAYE)
                .checkOutMontant(180.0)
                .reservation(reservation)
                .build();
         Notification notification = new Notification();
         reservation.getFactureList().add(facture);
        when(factureService.validerPaiementCheckOut(any(Reservation.class), anyDouble())).thenReturn(facture);
        when(notificationService.notifier(anyLong(),anyString())).thenReturn(notification);

        checkOutService.handlePaymentSuccess(1L);

        assertEquals(PaiementStatus.PAYE, reservationServicesList.get(0).getPaiementStatus());
    }


}
