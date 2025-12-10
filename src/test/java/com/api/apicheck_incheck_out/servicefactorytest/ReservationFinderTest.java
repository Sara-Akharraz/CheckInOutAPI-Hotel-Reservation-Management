package com.api.apicheck_incheck_out.servicefactorytest;


import com.api.apicheck_incheck_out.dto.DetailReservationRequestDTO;
import com.api.apicheck_incheck_out.dto.ReservationServiceRequestDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.factory.ReservationFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationFinderTest {
    @Mock
    ReservationRepository reservationRepository;
    @InjectMocks
    ReservationFinder reservationFinder;
    @Mock
    ReservationMapper reservationMapper;
    @Test
    void testGetReservationById() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        Reservation result = reservationFinder.findById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetReservationByIdThrowsException(){
        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()-> reservationFinder.findById(2L));
        assertEquals("Reservation non trouvée pour l'id :2",ex.getMessage());
        verify(reservationRepository,times(1)).findById(2L);
    }

    @Test
    void testGetReservationsByUserId() {
        Reservation r = new Reservation();
        when(reservationRepository.findByUserId(1L)).thenReturn(List.of(r));
        List<Reservation> result = reservationFinder.findByUser(1L);
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
        List<Reservation> result = reservationFinder.search("sara", today, today.plusDays(1), ReservationStatus.CONFIRMEE);
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

        List<Reservation> result = reservationFinder.search(null, today, today.plusDays(1), ReservationStatus.CONFIRMEE);

        assertEquals(1, result.size());
    }

    @Test
    void testGetReservationService() {
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

        List<ReservationServiceRequestDTO> result = reservationFinder.findServices(reservation);
        assertSame(reservation.getServiceList().size(),result.size());
        assertNotNull(result);
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

        DetailReservationRequestDTO result = reservationFinder.details(reservation, reservationMapper.toDTO(reservation), new ArrayList<>(), new ArrayList<>() );

        assertNotNull(result);
        assertEquals(reservation.getUser().getPrenom(),result.getUserLastName());
        verify(reservationMapper,times(1)).toDTO(any());
    }

    @Test
    void testVerifyExistanceThrowsException(){
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()-> reservationFinder.findById(1L));
        assertEquals("Reservation non trouvée pour l'id :1",ex.getMessage());
    }
    @Test
    void testFindUserByReservation() {
        Reservation reservation = new Reservation();
        User user = new User();
        reservation.setUser(user);
        when(reservationRepository.getById(1L)).thenReturn(reservation);
        User result = reservationFinder.findUserOfReservation(1L);
        assertNotNull(result);
    }

    @Test
    void testExistanteReservationThrowsException(){
        when(reservationRepository.existsById(2L)).thenReturn(false);
        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->reservationFinder.existante(2L));
        assertEquals("Reservation non trouvée pour l'id :2",ex.getMessage());
        verify(reservationRepository,times(1)).existsById(2L);
    }
}
