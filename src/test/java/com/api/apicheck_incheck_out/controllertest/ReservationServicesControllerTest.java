package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.ReservationServicesController;
import com.api.apicheck_incheck_out.dto.ReservationServiceRequestDTO;
import com.api.apicheck_incheck_out.dto.ReservationServicesDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.mapper.ReservationServicesMapper;
import com.api.apicheck_incheck_out.repository.NotificationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.NotificationService;
import com.api.apicheck_incheck_out.service.ReservationServicesService;
import com.api.apicheck_incheck_out.service.impl.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
 class ReservationServicesControllerTest {

    @Mock
    private ReservationServicesService reservationServicesService;

    @Mock
    private ReservationServicesMapper reservationServicesMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private ReservationServicesController controller;

    private Reservation reservation;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(10L);
        user.setEmail("freePalestine@email.com");

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
    }

    @Test
    void testGetServicesByReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Services s = new Services();
        s.setId(5L);
        s.setNom("Spa");
        s.setDescription("Relax");
        s.setPrix(100.0);

        ReservationServices rs = new ReservationServices();
        rs.setService(s);
        rs.setPaiementStatus(PaiementStatus.EN_ATTENTE);

        when(reservationServicesService.getAllServicesByReservation(1L)).thenReturn(List.of(rs));

        ResponseEntity<List<ReservationServiceRequestDTO>> response = controller
                .getServicesByReservation(1L, 10L);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Spa", response.getBody().get(0).getServiceName());
    }
    @Test
    void testGetServicesByReservation_Forbidden() {
        User otherUser = new User();
        otherUser.setId(2L);
        reservation.setUser(otherUser);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Exception exception = assertThrows(Exception.class, () ->
                controller.getServicesByReservation(1L, 10L)
        );
        assertTrue(exception.getMessage().contains("Vous n'avez pas accès"));
    }
    @Test
    void testAddSejourServices() {
        List<Long> serviceIds = List.of(1L, 2L);
        ResponseEntity<String> response = controller.addSejourServices(1L, serviceIds);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Services ajoutés avec succès !", response.getBody());
        verify(reservationServicesService).addSejourServicesToReservation(1L, serviceIds);
    }
    @Test
    void testGetServicesByPhase() {
        ReservationServices rs = new ReservationServices();
        ReservationServicesDTO dto = new ReservationServicesDTO();

        when(reservationServicesService.getServicesByPhase(1L, PhaseAjoutService.CHECK_IN))
                .thenReturn(List.of(rs));
        when(reservationServicesMapper.toDTO(rs)).thenReturn(dto);

        ResponseEntity<List<ReservationServicesDTO>> response = controller.getServicesByPhase(1L, PhaseAjoutService.CHECK_IN);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(reservationServicesMapper).toDTO(rs);
    }
    @Test
    void testAddReservationServices() {

        reservation.setChambreReservations(List.of());
        reservation.setServiceList(List.of());

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        Notification notif = new Notification();
        when(notificationService.notifier(any(), any())).thenReturn(notif);

        List<Long> serviceIds = List.of(5L, 6L);
        ResponseEntity<String> response = controller.addReservationServices(1L, serviceIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Services traités et email envoyé.", response.getBody());

        verify(reservationServicesService).addResService(1L, serviceIds);
        verify(emailSenderService).sendEmail(any(), any(), any());
        verify(notificationRepository).save(notif);
    }
    @Test
    void testGetAvailableServices() {
        Services s = new Services();
        s.setId(1L);
        when(reservationServicesService.getAvailableServices(1L)).thenReturn(List.of(s));

        ResponseEntity<List<Services>> response = controller.getAvailableServices(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
    @Test
    void testFindUnpaidServicesDuringStay() {
        Services s = new Services();
        s.setId(2L);
        when(reservationServicesService.getRsrvServicesSejourUnpaid(1L)).thenReturn(List.of(s));

        ResponseEntity<List<Services>> response = controller.findUnpaidServicesDuringStay(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
    @Test
    void testFindUnpaidServicesDuringStay_NotFound() {
        when(reservationServicesService.getRsrvServicesSejourUnpaid(1L))
                .thenThrow(new ReservationNotFoundException("Resrvation not found"));

        Exception exception = assertThrows(ReservationNotFoundException.class, () ->
                controller.findUnpaidServicesDuringStay(1L)
        );
        assertEquals("Resrvation not found", exception.getMessage());
    }
    @Test
    void testAddReservationServices_ReservationNotFoundException(){
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ReservationNotFoundException ex=assertThrows(ReservationNotFoundException.class,()->controller.addReservationServices(1L,List.of(1L,2L)));

        assertEquals("Réservation non trouvée avec l'id :1",ex.getMessage());

    }


}
