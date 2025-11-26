package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.*;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;

import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;

import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;
import com.api.apicheck_incheck_out.repository.*;
import com.api.apicheck_incheck_out.service.impl.ReservationServiceImpl;
import com.api.apicheck_incheck_out.service.NotificationService;

import jakarta.persistence.EntityExistsException;
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
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(1));
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
    void testAddReservationThrowsException(){
        Reservation reservation=new Reservation();
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(1));
        List<Long> chambreIds=List.of(1L);

        when(chambreReservationRepository.findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(any(), any(), any())).thenReturn(List.of(new ChambreReservation()));

        EntityExistsException ex=assertThrows(EntityExistsException.class,()->reservationService.addReservation(reservation,chambreIds));

        assertEquals("Une réservation existe déjà pour ces chambres avec les mêmes dates.", ex.getMessage());

        verify(chambreReservationRepository,times(1)).findByChambre_IdInAndReservation_DateDebutAndReservation_DateFin(any(),any(),any());
    }
    @Test
    void testAddReservation_ChambreNotFound(){
        Reservation reservation=new Reservation();
        reservation.setId(1L);
        List<Long> chambreIds=List.of(1L);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(chambreRepository.findById(1L)).thenReturn(Optional.empty());
        ChambreNotFoundException ex = assertThrows(ChambreNotFoundException.class, () ->
                reservationService.addReservation(reservation, chambreIds)
        );

        assertEquals("Chambre non trouvée dans la base de données : 1", ex.getMessage());

        verify(chambreReservationRepository, never()).save(any(ChambreReservation.class));
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
        Reservation result = reservationService.updateReservationStatus(1L, ReservationStatus.CONFIRMEE);
        assertEquals(ReservationStatus.CONFIRMEE, result.getStatus());
    }

    @Test
    void testUpdateReservationStatusThrowsException(){
        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());

        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->reservationService.updateReservationStatus(2L,ReservationStatus.CONFIRMEE));
        assertEquals("Reservation non trouvée pour l'id :2",ex.getMessage());

        verify(reservationRepository,times(1)).findById(2L);
        verify(reservationRepository,never()).save(any(Reservation.class));
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
                () -> reservationService.updateReservationStatus(1L, ReservationStatus.CONFIRMEE));

        assertEquals("Chambre non trouvée dans la base de données : 2", ex.getMessage());
    }
    @Test
    void testUpdateReservationStatus_NotConfirmee() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setChambreReservations(List.of());
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);

        Reservation result = reservationService.updateReservationStatus(1L, ReservationStatus.ANNULEE);

        assertEquals(ReservationStatus.ANNULEE, result.getStatus());

        verify(chambreReservationRepository, never()).save(any());
    }

    @Test
    void testDeleteReservation() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        reservationService.deleteReservation(1L);
        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void testDeleteReservationThrowsException(){
        when(reservationRepository.existsById(2L)).thenReturn(false);
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->reservationService.deleteReservation(2L));
        assertEquals("Reservation non trouvée pour l'id :2",ex.getMessage());
        verify(reservationRepository,times(1)).existsById(2L);
        verify(reservationRepository,never()).deleteById(2L);
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
    void testGetReservationByIdThrowsException(){
        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->reservationService.getReservationById(2L));
        assertEquals("Reservation non trouvée pour l'id :2",ex.getMessage());
        verify(reservationRepository,times(1)).findById(2L);

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
        LocalDate today= LocalDate.now();
        Reservation res1 = new Reservation();
        User user1 = new User();
        user1.setNom("sara");
        res1.setUser(user1);
        res1.setId(1L);
        res1.setDateDebut(LocalDate.now());
        res1.setDateFin(LocalDate.now().plusDays(1));
        res1.setStatus(ReservationStatus.CONFIRMEE);


        Reservation res2=new Reservation();
        res2.setId(2L);
        User user2=new User();
        user2.setNom("hasna");
        res2.setUser(user2);
        res2.setDateDebut(today);
        res2.setDateFin(today.plusDays(1));
        res2.setStatus(ReservationStatus.CONFIRMEE);

        //dateDebut avant filter
        Reservation res3 = new Reservation();
        res3.setId(3L);
        res3.setDateDebut(today.minusDays(2));
        res3.setDateFin(today.plusDays(1));
        res3.setStatus(ReservationStatus.CONFIRMEE);

        //status different
        Reservation res4 = new Reservation();
        res4.setId(4L);
        res4.setDateDebut(today);
        res4.setDateFin(today.plusDays(1));
        res4.setStatus(ReservationStatus.ANNULEE);

        when(reservationRepository.findAll()).thenReturn(List.of(res1,res2,res3,res4));
        List<Reservation> result = reservationService.searchReservations("sara", today, today.plusDays(1), ReservationStatus.CONFIRMEE);
        assertEquals(1, result.size());
        assertEquals(1L,result.get(0).getId());
    }
    @Test
    void testSearchReservations_NoSearchFilter() {
        LocalDate today = LocalDate.now();
        Reservation res = new Reservation();
        res.setId(1L);
        res.setDateDebut(today);
        res.setDateFin(today.plusDays(1));
        res.setStatus(ReservationStatus.CONFIRMEE);

        when(reservationRepository.findAll()).thenReturn(List.of(res));

        List<Reservation> result = reservationService.searchReservations(null, today, today.plusDays(1), ReservationStatus.CONFIRMEE);

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

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationMapper.toDTO(any())).thenReturn(new ReservationDTO());
        when(chambreMapper.toDTO(any())).thenReturn(new ChambreDTO());
        DetailReservationRequestDTO result = reservationService.getReservationDetail(1L);
        assertNotNull(result);

        verify(reservationRepository,times(1)).findById(1L);
        verify(reservationMapper,times(1)).toDTO(any());
        verify(chambreMapper,times(1)).toDTO(any());
    }
    @Test
    void testGetReservationDetailThrowsException(){
        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());

        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->reservationService.getReservationDetail(2L));

        assertEquals("Réservation introuvable avec l'id : 2",ex.getMessage());
        verify(reservationRepository,times(1)).findById(2L);

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
