package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.*;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;

import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;

import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;
import com.api.apicheck_incheck_out.repository.*;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationManager;
import com.api.apicheck_incheck_out.service.factory.ChambreStatutManager;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import com.api.apicheck_incheck_out.service.factory.ReservationFinder;
import com.api.apicheck_incheck_out.service.impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    ReservationFinder reservationFinder;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock private ReservationRepository reservationRepository;
    @Mock private ReservationMapper reservationMapper;
    @Mock private ChambreRepository chambreRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationService notificationService;
    @Mock private ReservationServiceRepository reservationServiceRepository;
    @Mock private ChambreReservationRepository chambreReservationRepository;
    @Mock private ChambreMapper chambreMapper;
    @Mock private CheckInRepository checkInRepository;
    @Mock private CheckOutRepository checkOutRepository;
    @Mock
    ReservationServicesFinder reservationServicesFinder;
    @Mock
    ChambreStatutManager chambreStatutManager;

    @Mock ChambreReservationManager chambreReservationManager;



    @Test
    void testAddReservation(){
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(1));
        Chambre chambre = new Chambre();
        chambre.setId(1L);
        chambre.setNom("A1");
        List<Long> chambreIds = List.of(1L);
        when(reservationServicesFinder.verifyForSameRooms(reservation,chambreIds)).thenReturn(reservation);
        when(chambreReservationManager.processChambreReservation(reservation,chambreIds)).thenReturn(reservation);
        Reservation result = reservationService.addReservation(reservation, chambreIds);
        assertNotNull(result);

    }
    @Test
    void testUpdateReservationStatus() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.CONFIRMEE);
        when(chambreStatutManager.newStatut(1L,ReservationStatus.CONFIRMEE)).thenReturn(reservation);
        Reservation result = reservationService.updateReservationStatus(1L, ReservationStatus.CONFIRMEE);
        assertEquals(ReservationStatus.CONFIRMEE, result.getStatus());
    }


    @Test
    void testDeleteReservation() {
        doNothing().when(reservationFinder).existante(1L);
        reservationService.deleteReservation(1L);
        verify(reservationRepository).deleteById(1L);
    }




    @Test
    void testGetReservationById() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationFinder.findById(1L)).thenReturn(reservation);
        Reservation result = reservationService.getReservationById(1L);
        assertEquals(1L, result.getId());
    }


    @Test
    void testGetReservationsByUserId() {
        Reservation r = new Reservation();
        when(reservationFinder.findByUser(1L)).thenReturn(List.of(r));
        List<Reservation> result = reservationService.getReservationsByUserId(1L);
        assertEquals(1, result.size());
    }



    @Test
    void testSearchReservations() {
        LocalDate today= LocalDate.now();
        Reservation res1 = new Reservation();
        User user1 = new User();
        user1.setNom("sara");
        res1.setUser(user1);
        res1.setId(1L);
        res1.setDateDebut(LocalDate.now());
        res1.setDateFin(LocalDate.now().plusDays(1));
        res1.setStatus(ReservationStatus.CONFIRMEE);

        when(reservationFinder.search("sara", today, today.plusDays(1), ReservationStatus.CONFIRMEE)).thenReturn(List.of(res1));

        List<Reservation> result = reservationService.searchReservations("sara", today, today.plusDays(1), ReservationStatus.CONFIRMEE);
        assertEquals(1, result.size());
        assertEquals(1L,result.get(0).getId());
    }


    @Test
    void testGetReservationDetail() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        User user = new User();
        user.setNom("Test");
        user.setPrenom("User");
        user.setCin("C123");
        user.setTelephone("123456");
        reservation.setUser(user);

        Services service=new Services();
        service.setNom("BreakFast");
        service.setDescription("Moroccan breakfast");
        service.setPrix(20.0);
        ReservationServices reservationServices=new ReservationServices();
        reservationServices.setId(100L);
        reservationServices.setService(service);
        reservationServices.setPaiementStatus(PaiementStatus.PAYE);

        reservation.setServiceList(List.of(reservationServices));

        ChambreReservation cr = new ChambreReservation();
        Chambre chambre = new Chambre();
        chambre.setNom("AAA");
        cr.setChambre(chambre);
        reservation.setChambreReservations(List.of(cr));


        when(reservationFinder.findById(1L)).thenReturn(reservation);
        when(reservationMapper.toDTO(any())).thenReturn(new ReservationDTO());
        DetailReservationRequestDTO d = new DetailReservationRequestDTO();
        d.setUserFirstName("Test");
        when(reservationFinder.findServices(reservation)).thenReturn(anyList());
        when(reservationFinder.details(reservation,reservationMapper.toDTO(reservation), new ArrayList<>(), new ArrayList<>())).thenReturn(d);

        DetailReservationRequestDTO result = reservationService.getReservationDetail(1L);

        assertNotNull(result);
        assertEquals(reservation.getUser().getNom(), result.getUserFirstName());
        verify(reservationFinder,times(1)).findById(1L);
        verify(reservationMapper,times(2)).toDTO(any());
        verify(reservationFinder,times(1)).findServices(reservation);
    }

    @Test
    void testExistsById() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        boolean result = reservationService.existsById(1L);
        assertTrue(result);
    }

    @Test
    void testFindUserByReservation() {
        User user = new User();
        user.setId(1L);
        when(reservationFinder.findUserOfReservation(1L)).thenReturn(user);

        User result = reservationService.findUserByReservation(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(reservationFinder).findUserOfReservation(1L);
    }

    @Test
    void testGetDashboardStats() {
        when(reservationRepository.count()).thenReturn(10L);
        when(checkInRepository.count()).thenReturn(5L);
        Map<String, Long> result = reservationService.getDashboardStats();
        assertEquals(10L, result.get("reservations"));
        assertEquals(5L, result.get("checkins"));
    }
}
