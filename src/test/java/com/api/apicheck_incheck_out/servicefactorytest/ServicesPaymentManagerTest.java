package com.api.apicheck_incheck_out.servicefactorytest;


import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;
import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import com.api.apicheck_incheck_out.repository.CheckOutRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.*;
import com.api.apicheck_incheck_out.service.factory.CheckOutFinder;
import com.api.apicheck_incheck_out.service.factory.ServicesPaymentManager;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServicesPaymentManagerTest {

    @Mock
    FactureService factureService;
    @Mock
    CheckOutRepository checkOutRepository;
    @InjectMocks
    ServicesPaymentManager servicesPaymentManager;
    @Mock
    ReservationServicesService reservationServicesService;
    @Mock
    UserService userService;
    @Mock
    ReservationServiceRepository reservationServiceRepository;
    CheckOut checkOut;
    List<ReservationServices> reservationServicesList = new ArrayList<>();

    @Mock
    CheckOutFinder checkOutFinder;
    @BeforeEach
    void setUp(){
        Reservation  reservation = Reservation.builder()
                .id(1L)
                .serviceList(reservationServicesList)
                .build();
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
        checkOut = CheckOut.builder()
                .id(1L)
                .reservation(reservation)
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .build();

        reservationServicesList.add(reservationServices);
        reservationServicesList.add(reservationServices1);
    }
    @Test
    void totalPriceTest(){

        when(checkOutFinder.findById(1L)).thenReturn(checkOut);
        when(reservationServicesService.getAllServicesByReservation(1L)).thenReturn(reservationServicesList);

        double calculatedAmount = servicesPaymentManager.totalPrice(1L);

        verify(checkOutFinder).findById(1L);
        verify(reservationServicesService).getAllServicesByReservation(1L);
        assertEquals(180, calculatedAmount);
    }


        @Test
        void handlePaymentSuccess(){
            User user = User.builder()
                    .id(1L)
                    .nom("Alami")
                    .prenom("Ali")
                    .cin("AB1234")
                    .telephone("0611221122")
                    .role(Role.CLIENT)
                    .build();
            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .user(user)
                    .status(ReservationStatus.CONFIRMEE)
                    .checkOut(checkOut)
                    .factureList(new ArrayList<>())
                    .serviceList(reservationServicesList)
                    .build();
            Facture facture = Facture.builder()
                    .type(FactureType.CHECK_OUT)
                    .status(PaiementStatus.PAYE)
                    .checkOutMontant(180.0)
                    .reservation(reservation)
                    .build();
            checkOut.setReservation(reservation);

            reservationServicesList.get(0).setPaiementStatus(PaiementStatus.PAYE);
            reservationServicesList.get(1).setPaiementStatus(PaiementStatus.PAYE);

             when(reservationServicesService.getServicesByPhase(1L, PhaseAjoutService.SEJOUR)).thenReturn(reservationServicesList);

            when(reservationServiceRepository.saveAll(anyList())).thenReturn(reservationServicesList);

            reservation.getFactureList().add(facture);
            when(checkOutFinder.findByReservation(1L)).thenReturn(checkOut);
            when(checkOutFinder.findById(1L)).thenReturn(checkOut);

            servicesPaymentManager.handleServicesPayment(1L);

            assertEquals(PaiementStatus.PAYE, reservationServicesList.get(0).getPaiementStatus());

        }

}
