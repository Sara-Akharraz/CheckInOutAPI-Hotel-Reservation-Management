package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.DTO.PaiementRequestDTO;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.Repository.FactureRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.Service.Impl.FactureServiceImpl;
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
        when(reservation.getDate_debut()).thenReturn(LocalDate.now());
        when(reservation.getDate_fin()).thenReturn(LocalDate.now().plusDays(1));
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
        when(reservation.getDate_debut()).thenReturn(debut);
        when(reservation.getDate_fin()).thenReturn(fin);

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
        when(reservationServiceRepository.findByReservationAndPhase(anyLong(), eq(PhaseAjoutService.check_in))).thenReturn(services);

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
    void testPayerFactureCheckInCache() {
        Reservation reservation = mock(Reservation.class);
        when(reservation.getFactureList()).thenReturn(new ArrayList<>());
        when(reservation.getServiceList()).thenReturn(Collections.emptyList());
        when(reservation.getDate_debut()).thenReturn(LocalDate.now());
        when(reservation.getDate_fin()).thenReturn(LocalDate.now().plusDays(1));
        when(reservation.getChambreReservations()).thenReturn(Collections.emptyList());

        factureService.payerFactureCheckInCache(reservation);

        verify(factureRepository).save(any());
        verify(reservationRepository).save(reservation);
    }
}
