package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.InvalidReservationStatusException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationFinder;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationStatusManager;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class ChambreReservationStatusManagerTest {
    @Mock
    private ChambreReservationRepository chambreReservationRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ChambreReservationFinder finder;
    @Mock
    private ReservationServicesFinder resfinder;
    @InjectMocks
    private ChambreReservationStatusManager manager;

    private Reservation reservation;
    private ChambreReservation chambreReservation1;
    private ChambreReservation chambreReservation2;
    private Chambre chambre;

    @BeforeEach
    void setUp(){
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.CONFIRMEE);

        chambre = new Chambre();
        chambre.setId(10L);

        chambreReservation1 = new ChambreReservation();
        chambreReservation1.setId(100L);
        chambreReservation1.setStatut(ChambreStatut.RESERVED);
        chambreReservation1.setChambre(chambre);
        chambreReservation1.setReservation(reservation);

        chambreReservation2 = new ChambreReservation();
        chambreReservation2.setId(200L);
        chambreReservation2.setStatut(ChambreStatut.RESERVED);
        chambreReservation2.setReservation(reservation);
    }


    @Test
    void testSetChambreOccupee_WithConfirmedReservation_UpdatesAllChambresToOccupee() {
        Long idReservation = 1L;
        List<ChambreReservation> chambreReservations = List.of(chambreReservation1, chambreReservation2);

        when(resfinder.findReservationById(idReservation)).thenReturn(reservation);
        when(finder.findChambreReservationByReservationId(idReservation)).thenReturn(chambreReservations);

        manager.setChambreOccupee(idReservation);

        verify(resfinder).findReservationById(idReservation);
        verify(finder).findChambreReservationByReservationId(idReservation);
        verify(chambreReservationRepository, times(2)).save(any(ChambreReservation.class));

        ArgumentCaptor<ChambreReservation> captor = ArgumentCaptor.forClass(ChambreReservation.class);
        verify(chambreReservationRepository, times(2)).save(captor.capture());

        List<ChambreReservation> savedReservations = captor.getAllValues();
        assertEquals(2, savedReservations.size());
        assertTrue(savedReservations.stream().allMatch(cr -> cr.getStatut() == ChambreStatut.OCCUPEE));
        assertTrue(savedReservations.stream().allMatch(cr -> cr.getReservation() == reservation));
    }
    @Test
    void testSetChambreOccupee_WithNonConfirmedReservation_ThrowsException() {

        Long idReservation = 1L;
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        when(resfinder.findReservationById(idReservation)).thenReturn(reservation);

        InvalidReservationStatusException exception = assertThrows(
                InvalidReservationStatusException.class,
                () -> manager.setChambreOccupee(idReservation)
        );

        assertTrue(exception.getMessage().contains("La réservation n'est pas CONFIRMEE"));
        assertTrue(exception.getMessage().contains("EN_ATTENTE"));
        verify(finder, never()).findChambreReservationByReservationId(anyLong());
        verify(chambreReservationRepository, never()).save(any());
    }
    @Test
    void testSetChambreDisponible_WithConfirmedReservation_UpdatesAllChambresAndReservation() {
        Long idReservation = 1L;
        List<ChambreReservation> chambreReservations = List.of(chambreReservation1, chambreReservation2);

        when(resfinder.findReservationById(idReservation)).thenReturn(reservation);
        when(finder.findChambreReservationsByReservation(reservation)).thenReturn(chambreReservations);

        manager.setChambreDisponible(idReservation);

        verify(resfinder).findReservationById(idReservation);
        verify(finder).findChambreReservationsByReservation(reservation);
        verify(chambreReservationRepository, times(2)).save(any(ChambreReservation.class));
        verify(reservationRepository).save(reservation);

        ArgumentCaptor<ChambreReservation> chambreCaptor = ArgumentCaptor.forClass(ChambreReservation.class);
        verify(chambreReservationRepository, times(2)).save(chambreCaptor.capture());

        List<ChambreReservation> savedChambres = chambreCaptor.getAllValues();
        assertTrue(savedChambres.stream().allMatch(cr -> cr.getStatut() == ChambreStatut.DISPONIBLE));
        assertTrue(savedChambres.stream().allMatch(cr -> cr.getReservation() == null));

        assertEquals(ReservationStatus.TERMINEE, reservation.getStatus());
    }
    @Test
    void testSetChambreDisponible_WithNonConfirmedReservation_ThrowsException() {
        Long idReservation = 1L;
        reservation.setStatus(ReservationStatus.TERMINEE);

        when(resfinder.findReservationById(idReservation)).thenReturn(reservation);

        InvalidReservationStatusException exception = assertThrows(
                InvalidReservationStatusException.class,
                () -> manager.setChambreDisponible(idReservation)
        );

        assertTrue(exception.getMessage().contains("La réservation n'est pas CONFIRMEE"));
        verify(finder, never()).findChambreReservationsByReservation(any());
        verify(chambreReservationRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }
    @Test
    void testUpdateReservationStatus_UpdatesStatusAndSaves() {

        ReservationStatus newStatus = ReservationStatus.ANNULEE;

        manager.updateReservationStatus(reservation, newStatus);

        assertEquals(ReservationStatus.ANNULEE, reservation.getStatus());
        verify(reservationRepository).save(reservation);
    }
    @Test
    void testSetChambreReserved_WithEnAttenteReservation_UpdatesChambreToReserved() {
        Long idChambre = 10L;
        Long idReservation = 1L;
        reservation.setStatus(ReservationStatus.EN_ATTENTE);

        when(finder.findChambreReservation(idReservation, idChambre)).thenReturn(chambreReservation1);
        when(resfinder.findReservationById(idReservation)).thenReturn(reservation);

        manager.setChambreReserved(idChambre, idReservation);

        verify(finder).findChambreReservation(idReservation, idChambre);
        verify(resfinder).findReservationById(idReservation);
        verify(chambreReservationRepository).save(chambreReservation1);

        assertEquals(ChambreStatut.RESERVED, chambreReservation1.getStatut());
        assertEquals(reservation, chambreReservation1.getReservation());
    }
    @Test
    void testSetChambreReserved_WithConfirmedReservation_ThrowsException() {
        Long idChambre = 10L;
        Long idReservation = 1L;
        reservation.setStatus(ReservationStatus.CONFIRMEE);

        when(finder.findChambreReservation(idReservation, idChambre)).thenReturn(chambreReservation1);
        when(resfinder.findReservationById(idReservation)).thenReturn(reservation);

        InvalidReservationStatusException exception = assertThrows(
                InvalidReservationStatusException.class,
                () -> manager.setChambreReserved(idChambre, idReservation)
        );

        assertTrue(exception.getMessage().contains("La réservation n'est pas EN_ATTENTE"));
        assertTrue(exception.getMessage().contains("CONFIRMEE"));
        verify(chambreReservationRepository, never()).save(any());
    }


}
