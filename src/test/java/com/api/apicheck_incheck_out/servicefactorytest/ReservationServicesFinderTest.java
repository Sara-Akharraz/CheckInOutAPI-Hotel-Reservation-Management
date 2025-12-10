package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServicesFinderTest {

    @Mock
    ChambreReservationRepository chambreReservationRepository;
    @InjectMocks
    ReservationServicesFinder reservationServicesFinder;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ChambreRepository chambreRepository;
    @Mock
    ReservationService reservationService;

    @Test
    void testProcessAddReservation() {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(1));
        Chambre chambre = new Chambre();
        chambre.setId(1L);
        chambre.setNom("A1");
        List<Long> chambreIds = List.of(1L);
        when(chambreReservationRepository.findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(any(), any(), any())).thenReturn(List.of());
        Reservation result = reservationServicesFinder.verifyForSameRooms(reservation, chambreIds);
        assertNotNull(result);
    }

    @Test
    void testProcessAddReservationThrowsException(){
        Reservation reservation=new Reservation();
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(1));
        List<Long> chambreIds=List.of(1L);

        when(chambreReservationRepository.findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(any(), any(), any())).thenReturn(List.of(new ChambreReservation()));

        EntityExistsException ex=assertThrows(EntityExistsException.class,()->reservationServicesFinder.verifyForSameRooms(reservation,chambreIds));

        assertEquals("Une réservation existe déjà pour ces chambres avec les mêmes dates.", ex.getMessage());

        verify(chambreReservationRepository,times(1)).findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(any(),any(),any());
    }
}
