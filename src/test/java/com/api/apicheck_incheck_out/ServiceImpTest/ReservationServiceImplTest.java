package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.DTO.*;
import com.api.apicheck_incheck_out.Entity.*;
import com.api.apicheck_incheck_out.Enums.*;
import com.api.apicheck_incheck_out.Mapper.ChambreMapper;
import com.api.apicheck_incheck_out.Mapper.ReservationMapper;
import com.api.apicheck_incheck_out.Repository.*;
import com.api.apicheck_incheck_out.Service.Impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddReservation() {
        Reservation reservation = new Reservation();
        reservation.setDate_debut(LocalDate.now());
        reservation.setDate_fin(LocalDate.now().plusDays(1));
        Chambre chambre = new Chambre();
        chambre.setId(1L);
        chambre.setNom("A1");
        List<Long> chambreIds = List.of(1L);
        when(chambreReservationRepository.findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(any(), any(), any())).thenReturn(List.of());
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        Reservation result = reservationService.addReservation(reservation, chambreIds);
        assertNotNull(result);
    }

    @Test
    void testUpdateReservationStatus() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        ChambreReservation cr = new ChambreReservation();
        cr.setId(2L);
        reservation.setChambreReservations(List.of(cr));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(chambreReservationRepository.findById(2L)).thenReturn(Optional.of(cr));
        when(reservationRepository.save(any())).thenReturn(reservation);
        Reservation result = reservationService.updateReservationStatus(1L, ReservationStatus.Confirmee);
        assertEquals(ReservationStatus.Confirmee, result.getStatus());
    }

    @Test
    void testDeleteReservation() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        reservationService.deleteReservation(1L);
        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void testGetReservationById() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        Reservation result = reservationService.getReservationById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetReservationsByUserId() {
        Reservation r = new Reservation();
        when(reservationRepository.findByUserId(1L)).thenReturn(List.of(r));
        List<Reservation> result = reservationService.getReservationsByUserId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchReservations() {
        Reservation reservation = new Reservation();
        User user = new User();
        user.setNom("sara");
        reservation.setUser(user);
        reservation.setId(1L);
        reservation.setDate_debut(LocalDate.now());
        reservation.setDate_fin(LocalDate.now().plusDays(1));
        reservation.setStatus(ReservationStatus.Confirmee);
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        List<Reservation> result = reservationService.searchReservations("sara", LocalDate.now(), LocalDate.now().plusDays(1), ReservationStatus.Confirmee);
        assertEquals(1, result.size());
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
        reservation.setServiceList(new ArrayList<>());
        ChambreReservation cr = new ChambreReservation();
        Chambre chambre = new Chambre();
        chambre.setNom("AAA");
        cr.setChambre(chambre);
        reservation.setChambreReservations(List.of(cr));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationMapper.toDTO(any())).thenReturn(new ReservationDTO());
        when(chambreMapper.toDTO(any())).thenReturn(new ChambreDTO());
        DetailReservationRequestDTO result = reservationService.getReservationDetail(1L);
        assertNotNull(result);
    }

    @Test
    void testExistsById() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        boolean result = reservationService.existsById(1L);
        assertTrue(result);
    }

    @Test
    void testFindUserByReservation() {
        Reservation reservation = new Reservation();
        User user = new User();
        reservation.setUser(user);
        when(reservationRepository.getById(1L)).thenReturn(reservation);
        User result = reservationService.findUserByReservation(1L);
        assertNotNull(result);
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
