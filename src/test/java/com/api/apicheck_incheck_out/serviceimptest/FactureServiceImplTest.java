package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.PaiementRequestDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.FactureType;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.exceptionhandling.PaymentValidationException;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.repository.FactureRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.service.impl.FactureServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class FactureServiceImplTest {

    @InjectMocks
    FactureServiceImpl factureService;

    @Mock
    FactureRepository factureRepository;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    ReservationServiceRepository reservationServiceRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPayerFactureCheckIn_void() {
        Reservation reservation = mock(Reservation.class);
        when(reservation.getFactureList()).thenReturn(new ArrayList<>());
        when(reservation.getDateDebut()).thenReturn(LocalDate.now());
        when(reservation.getDateFin()).thenReturn(LocalDate.now().plusDays(1));
        when(reservation.getChambreReservations()).thenReturn(Collections.emptyList());

        when(reservationRepository.save(reservation)).thenReturn(reservation);
        factureService.payerFactureCheckIn(reservation);

        verify(factureRepository).save(any());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testCalculerMontantCheckIn() {
        Reservation reservation = mock(Reservation.class);
        LocalDate debut = LocalDate.now();
        LocalDate fin = debut.plusDays(2);
        when(reservation.getDateDebut()).thenReturn(debut);
        when(reservation.getDateFin()).thenReturn(fin);

        Chambre chambre = new Chambre();
        chambre.setPrix(100);
        ChambreReservation cr = mock(ChambreReservation.class);
        when(cr.getChambre()).thenReturn(chambre);
        List<ChambreReservation> chambreReservations = Collections.singletonList(cr);
        when(reservation.getChambreReservations()).thenReturn(chambreReservations);

        ReservationServices service = mock(ReservationServices.class);
        Services s = mock(Services.class);
        when(s.getPrix()).thenReturn(50.0);
        when(service.getService()).thenReturn(s);
        List<ReservationServices> services = Collections.singletonList(service);
        when(reservationServiceRepository.findByReservationAndPhase(anyLong(), eq(PhaseAjoutService.CHECK_IN))).thenReturn(services);

        double montant = factureService.calculerMontantCheckIn(reservation);
        assertTrue(montant > 0);
    }

    @Test
    void testValiderPaiementStripe_success() throws StripeException {
        PaiementRequestDTO dto = new PaiementRequestDTO();
        dto.setClientSecret("secret");

        FactureServiceImpl spyService = spy(factureService);

        PaymentIntent pi = mock(PaymentIntent.class);
        when(pi.getStatus()).thenReturn("succeeded");

        try (MockedStatic<PaymentIntent> utilities = Mockito.mockStatic(PaymentIntent.class)) {
            utilities.when(() -> PaymentIntent.retrieve("secret")).thenReturn(pi);
            boolean result = spyService.validerPaiementStripe(dto);
            assertTrue(result);
        }
    }
    @Test
    void testValiderPaiementStripe_ExceptionEstLevee() {

        PaiementRequestDTO paiementRequest = new PaiementRequestDTO();
        paiementRequest.setClientSecret("pi_test_secret");
        try (MockedStatic<PaymentIntent> mockedPaymentIntent = mockStatic(PaymentIntent.class)) {
            mockedPaymentIntent.when(() -> PaymentIntent.retrieve(paiementRequest.getClientSecret()))
                    .thenThrow(new StripeException("Erreur de connexion à Stripe", "request_id", "code", 500) {});

            boolean resultat = factureService.validerPaiementStripe(paiementRequest);

            assertFalse(resultat, "Le résultat devrait être false en cas d'exception Stripe");

            mockedPaymentIntent.verify(() -> PaymentIntent.retrieve(paiementRequest.getClientSecret()), times(1));
        }
    }

    @Test
    void testPayerFactureCheckInCache() {
        Reservation reservation = mock(Reservation.class);
        when(reservation.getFactureList()).thenReturn(new ArrayList<>());
        when(reservation.getServiceList()).thenReturn(Collections.emptyList());
        when(reservation.getDateDebut()).thenReturn(LocalDate.now());
        when(reservation.getDateFin()).thenReturn(LocalDate.now().plusDays(1));
        when(reservation.getChambreReservations()).thenReturn(Collections.emptyList());

        factureService.payerFactureCheckInCache(reservation);

        verify(factureRepository).save(any());
        verify(reservationRepository).save(reservation);
    }
    @Test
    void testValiderPaiementCheckOut_success() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        double total = 300.0;

        Facture factureSaved = Facture.builder()
                .id(10L)
                .type(FactureType.CHECK_OUT)
                .status(PaiementStatus.PAYE)
                .checkOutMontant(total)
                .reservation(reservation)
                .build();

        when(factureRepository.save(any(Facture.class))).thenReturn(factureSaved);

        Facture result = factureService.validerPaiementCheckOut(reservation, total);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(FactureType.CHECK_OUT, result.getType());
        assertEquals(PaiementStatus.PAYE, result.getStatus());
        assertEquals(total, result.getCheckOutMontant());

        verify(factureRepository, times(1)).save(any(Facture.class));
    }
    @Test
    void testValiderPaiementCheckOut_exception() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);

        double total = 200.0;

        when(factureRepository.save(any(Facture.class)))
                .thenThrow(new RuntimeException("DB error"));

        PaymentValidationException ex = assertThrows(
                PaymentValidationException.class,
                () -> factureService.validerPaiementCheckOut(reservation, total)
        );

        assertEquals("Failed to validate the checout payment", ex.getMessage());

        verify(factureRepository, times(1)).save(any(Facture.class));
    }
    @Test
    void testPayerFactureCheckInCache_WithServices() {

        Reservation reservation = mock(Reservation.class);

        when(reservation.getFactureList()).thenReturn(new ArrayList<>());

        when(reservation.getDateDebut()).thenReturn(LocalDate.now());
        when(reservation.getDateFin()).thenReturn(LocalDate.now().plusDays(1));
        when(reservation.getChambreReservations()).thenReturn(Collections.emptyList());

        ReservationServices service1 = mock(ReservationServices.class);
        ReservationServices service2 = mock(ReservationServices.class);

        List<ReservationServices> services = Arrays.asList(service1, service2);
        when(reservation.getServiceList()).thenReturn(services);

        factureService.payerFactureCheckInCache(reservation);

        verify(factureRepository).save(any(Facture.class));

        verify(service1).setPaiementStatus(PaiementStatus.PAYE);
        verify(service2).setPaiementStatus(PaiementStatus.PAYE);

        verify(reservationServiceRepository).save(service1);
        verify(reservationServiceRepository).save(service2);

        verify(reservationRepository).save(reservation);
    }

    @Test
    void testPayerFactureCheckIn_ReservationNotFound() {
        PaiementRequestDTO dto = new PaiementRequestDTO();
        dto.setReservationId(999L);
        dto.setMethod("STRIPE");

        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        ReservationNotFoundException exception = assertThrows(
                ReservationNotFoundException.class,
                () -> factureService.payerFactureCheckIn(dto)
        );

        assertEquals("Reservation not found", exception.getMessage());
        verify(factureRepository, never()).save(any());
    }
    @Test
    void testPayerFactureCheckIn_success() {

        PaiementRequestDTO dto = new PaiementRequestDTO();
        dto.setReservationId(999L);
        dto.setMethod("STRIPE");

        Reservation res = mock(Reservation.class);
        when(res.getFactureList()).thenReturn(new ArrayList<>());
        ReservationServices reservationService = mock(ReservationServices.class);
        when(res.getServiceList()).thenReturn(Arrays.asList(reservationService));

        when(reservationRepository.findById(999L)).thenReturn(Optional.of(res));

        FactureServiceImpl spyService = spy(factureService);

        doReturn(500.0).when(spyService).calculerMontantCheckIn(res);
        doReturn(true).when(spyService).validerPaiementStripe(dto);

        Boolean result = spyService.payerFactureCheckIn(dto);


        assertTrue(result);

        verify(factureRepository).save(any(Facture.class));

        verify(reservationService).setPaiementStatus(PaiementStatus.PAYE);
        verify(reservationServiceRepository).save(any(ReservationServices.class));

        verify(reservationRepository).save(res);

        assertEquals(1, res.getFactureList().size());
    }





}

