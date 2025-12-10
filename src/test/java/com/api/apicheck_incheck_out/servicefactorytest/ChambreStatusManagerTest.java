package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.factory.ChambreStatutManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChambreStatusManagerTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ChambreReservationRepository chambreReservationRepository;

    @InjectMocks
    ChambreStatutManager chambreStatutManager;

    @Test
    void testUpdateReservationStatus() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        ChambreReservation cr = new ChambreReservation();
        cr.setId(2L);
        reservation.setChambreReservations(List.of(cr));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(chambreReservationRepository.findById(2L)).thenReturn(Optional.of(cr));
        when(chambreReservationRepository.save(cr)).thenReturn(cr);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        Reservation result = chambreStatutManager.newStatut(1L, ReservationStatus.CONFIRMEE);

        assertEquals(ReservationStatus.CONFIRMEE, result.getStatus());
        verify(chambreReservationRepository, times(1)).save(any());
        verify(chambreReservationRepository, times(1)).findById(any());
        verify(reservationRepository, times(1)).findById(any());
        verify(reservationRepository, times(1)).save(any());

    }

    @Test
    void testUpdateReservationStatus_ChambreNotFound() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        ChambreReservation cr = new ChambreReservation();
        cr.setId(2L);
        reservation.setChambreReservations(List.of(cr));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(chambreReservationRepository.findById(2L)).thenReturn(Optional.empty());

        ChambreNotFoundException ex = assertThrows(ChambreNotFoundException.class,
                () -> chambreStatutManager.newStatut(1L, ReservationStatus.CONFIRMEE));

        assertEquals("Chambre non trouvée dans la base de données : 2", ex.getMessage());
    }
    @Test
    void testUpdateReservationStatus_NotConfirmee() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setChambreReservations(List.of());
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);

        Reservation result = chambreStatutManager.newStatut(1L, ReservationStatus.ANNULEE);

        assertEquals(ReservationStatus.ANNULEE, result.getStatus());

        verify(chambreReservationRepository, never()).save(any());
    }
}
