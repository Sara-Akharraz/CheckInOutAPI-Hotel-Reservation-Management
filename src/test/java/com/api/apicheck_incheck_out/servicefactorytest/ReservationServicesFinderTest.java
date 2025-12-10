package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
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
import java.util.Set;

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
    void testFindReservationById_ReservationNotFoundException() {

        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> {
            reservationServicesFinder.findReservationById(2L);
        });
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

    @Test
    void testFindReservationById(){
        Reservation reservation=new Reservation();
        reservation.setId(1L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result=reservationServicesFinder.findReservationById(1L);
        assertNotNull(result);
        assertEquals(1L,result.getId());
    }

    @Test
    void testExtractExistingServiceIds() {

        Services service1 = new Services();
        service1.setId(10L);

        Services service2 = new Services();
        service2.setId(20L);

        ReservationServices rs1 = new ReservationServices();
        rs1.setService(service1);

        ReservationServices rs2 = new ReservationServices();
        rs2.setService(service2);


        Reservation reservation = new Reservation();
        reservation.setServiceList(List.of(rs1, rs2));

        Set<Long> result = reservationServicesFinder.extractExistingServiceIds(reservation);

        assertEquals(Set.of(10L, 20L), result);
        assertTrue(result.contains(10L));
        assertTrue(result.contains(20L));
    }
    @Test
    void testExtractExistingServiceIds_EmptyList() {

        Reservation reservation = new Reservation();
        reservation.setServiceList(List.of());

        Set<Long> result = reservationServicesFinder.extractExistingServiceIds(reservation);

        assertTrue(result.isEmpty());
    }
}
