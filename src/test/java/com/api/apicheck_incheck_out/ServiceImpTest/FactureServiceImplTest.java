package com.api.apicheck_incheck_out.ServiceImpTest;


import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Facture;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Repository.FactureRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.Impl.FactureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FactureServiceImplTest {
    @Mock
    private FactureRepository factureRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private FactureServiceImpl factureService;
    private Reservation reservation;

    @BeforeEach
    void setup(){
        Chambre chambre1=new Chambre();
        chambre1.setPrix(250.0);

        Chambre chambre2=new Chambre();
        chambre2.setPrix(100.0);

        User user=new User();
        user.setStripeId("testing");

        reservation=new Reservation();
        reservation.setUser(user);
        reservation.setChambreList(Arrays.asList(chambre1,chambre2));
        reservation.setFactureList(new ArrayList<>());
        reservation.setDate_debut(LocalDate.of(2025, 4, 10));
        reservation.setDate_fin(LocalDate.of(2025, 4, 12));
    }
    @Test
    public void testPayerFactureCheckIn_StripePayment() {
        FactureServiceImpl spyService = Mockito.spy(factureService);

        Reservation reservation = new Reservation();
        reservation.setDate_debut(LocalDate.now());
        reservation.setDate_fin(LocalDate.now().plusDays(1));
        reservation.setFactureList(new ArrayList<>());


        Chambre chambre = new Chambre();
        chambre.setId(1L);
        reservation.setChambreList(List.of(chambre));

        doReturn(true).when(spyService).validerPaiementStripe(anyDouble(), eq(reservation));

        when(factureRepository.save(any(Facture.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Boolean result = spyService.payerFactureCheckIn(reservation, PaiementMethod.STRIPE);

        assertTrue(result);
        verify(factureRepository, times(1)).save(any(Facture.class));
        verify(reservationRepository, times(1)).save(reservation);
        assertEquals(1, reservation.getFactureList().size());
        assertEquals(PaiementStatus.paye, reservation.getFactureList().get(0).getStatus());
    }

    @Test
    public void TestClaculerMontantCheckIn(){
        double result =factureService.calculerMontantCheckIn(reservation);
        double expected=(250+100)*2*1.2+10;
        assertEquals(expected,result,0.01);
    }
    @Test
    public void TestValiderPaiementStripe(){
        Reservation reservation=new Reservation();
        User user =new User();
        user.setStripeId("testing");
        reservation.setUser(user);

        Boolean result =factureService.validerPaiementStripe(500,reservation);
        assertFalse(result);
    }
//    @Test
//    public void TestValiderPaiementPaypal(){
//        Reservation reservation=new Reservation();
//        User user =new User();
//        user.setPaypalId("testing");
//        reservation.setUser(user);
//
//        Boolean result = factureService.validerPaiementPaypal(500, reservation);
//        assertFalse(result);
//    }

}

