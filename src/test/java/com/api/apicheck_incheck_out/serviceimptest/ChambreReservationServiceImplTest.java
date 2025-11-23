package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.impl.ChambreReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChambreReservationServiceImplTest {

    @Mock
    private ChambreReservationRepository chambreReservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    ChambreReservationServiceImpl chambreReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChambreStatut_Success() {
        Long reservationId = 1L;
        Long chambreId = 2L;

        ChambreReservation cr = new ChambreReservation();
        cr.setStatut(ChambreStatut.DISPONIBLE);

        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(reservationId, chambreId))
                .thenReturn(Optional.of(cr));

        ChambreStatut statut = chambreReservationService.getChambreStatut(reservationId, chambreId);

        assertEquals(ChambreStatut.DISPONIBLE, statut);
    }

    @Test
    void testGetChambreStatut_NotFound() {
        when(chambreReservationRepository.findByReservation_IdAndChambre_Id(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chambreReservationService.getChambreStatut(1L, 1L));

        assertTrue(exception.getMessage().contains("ChambreReservation non trouvée"));
    }

    @Test
    void testSetChambreOccupee_Success() {
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.Confirmee);

        ChambreReservation cr = new ChambreReservation();
        cr.setStatut(ChambreStatut.DISPONIBLE);

        List<ChambreReservation> chambreReservations = Collections.singletonList(cr);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(chambreReservationRepository.findByReservation_Id(reservationId)).thenReturn(chambreReservations);
        when(chambreReservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        chambreReservationService.setChambreOccupee(reservationId);

        assertEquals(ChambreStatut.OCCUPEE, cr.getStatut());
        verify(chambreReservationRepository, times(1)).save(cr);
    }

    @Test
    void testSetChambreOccupee_ReservationNotConfirmed() {
        Long reservationId = 1L;

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.En_Attente);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chambreReservationService.setChambreOccupee(reservationId));

        assertTrue(exception.getMessage().contains("La réservation n'est pas confirmée"));
    }



}
