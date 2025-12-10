package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.UserDto;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;
import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import com.api.apicheck_incheck_out.exceptionhandling.UserRegistrationException;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.service.factory.CheckOutNotificationManager;
import com.api.apicheck_incheck_out.service.factory.CheckOutStatusManager;
import com.api.apicheck_incheck_out.service.factory.ServicesPaymentManager;
import com.api.apicheck_incheck_out.service.factory.StripeResponseCreator;
import com.api.apicheck_incheck_out.service.impl.CheckOutServiceImpl;
import com.api.apicheck_incheck_out.service.impl.FactureServiceImpl;
import com.api.apicheck_incheck_out.service.impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.service.impl.UserServiceImpl;
import com.api.apicheck_incheck_out.stripe.CheckOutRequest;
import com.api.apicheck_incheck_out.stripe.StripeResponse;
import com.api.apicheck_incheck_out.stripe.service.impl.StripeServiceImpl;
import com.stripe.net.StripeRequest;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckOutServiceImplTest {

    @InjectMocks
    private CheckOutServiceImpl checkOutService;

    @Mock
    private ReservationRepository reservationRepository;


    @Mock
    private CheckOutRepository checkOutRepository;

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

    @Mock
    StripeResponseCreator stripeResponseCreator;
    @Mock
    ServicesPaymentManager servicesPaymentManager;
    @Mock
    CheckOutStatusManager checkOutStatusManager;
    List<CheckOut> checkouts = new ArrayDeque<>();
    List<ReservationServices>  reservationServicesList = new ArrayList<>();
    @Mock
    CheckOutNotificationManager checkOutNotificationManager;

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
    void addCheckOutTest(){
        when(checkOutStatusManager.newWaitedCheckOut(any(CheckOut.class))).thenReturn(checkOut);

        CheckOut addedCheckOut = checkOutService.addCheckOut(checkOut);

        assertNotNull(addedCheckOut);
        assertEquals(checkOut.getId(), addedCheckOut.getId());
        assertEquals(checkOut.getCheckOutStatut(), addedCheckOut.getCheckOutStatut());
        assertEquals(checkOut.getDateCheckOut(), addedCheckOut.getDateCheckOut());
    }



    @Test
    void getAllCheckOutsTest(){
        CheckOut checkOut1 = CheckOut.builder()
                .id(2L)
                .dateCheckOut(LocalDate.of(2025, 11, 30))
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
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
    void getAmount(){
        when(servicesPaymentManager.totalPrice(1L)).thenReturn(180.0);

        double calculatedAmount = checkOutService.getAmount(1L);

        verify(servicesPaymentManager).totalPrice(1L);
        assertEquals(180.0, calculatedAmount);
    }


    @Test
    void payer(){
        StripeResponse stripeResponse = StripeResponse.builder()
                .status("SUCCESS")
                .message("SUCCESS")
                .sessionId("1111")
                .sessionUrl("session-url")
                .build();

        when(stripeResponseCreator.create(1L,180.0)).thenReturn(stripeResponse);

        StripeResponse returnedStripeResponse =  stripeResponseCreator.create(1L,180.0);

        assertNotNull(returnedStripeResponse);
        assertEquals(stripeResponse.getStatus(), returnedStripeResponse.getStatus());
        assertEquals(stripeResponse.getSessionId(), returnedStripeResponse.getSessionId());
        assertEquals(stripeResponse.getSessionUrl(), returnedStripeResponse.getSessionUrl());
    }

    @Test
    void handlePaymentSuccess(){

        when(checkOutStatusManager.confirmed(1L)).thenReturn(checkOut);
        doNothing().when(servicesPaymentManager).handleServicesPayment(checkOut.getReservation().getId());

        facture = Facture.builder()
                .type(FactureType.CHECK_OUT)
                .status(PaiementStatus.PAYE)
                .checkOutMontant(180.0)
                .reservation(reservation)
                .build();

        reservation.getFactureList().add(facture);

        when(factureService.validerPaiementCheckOut(any(Reservation.class), anyDouble())).thenReturn(facture);
        doNothing().when(checkOutNotificationManager).PaymentConfirmed(1L,1L);

        checkOutService.handlePaymentSuccess(1L);

        verify(checkOutStatusManager).confirmed(1L);
        verify(servicesPaymentManager).handleServicesPayment(1L);
        verify(factureService).validerPaiementCheckOut(any(Reservation.class), anyDouble());
        verify(checkOutNotificationManager).PaymentConfirmed(1L, 1L);
    }




}
