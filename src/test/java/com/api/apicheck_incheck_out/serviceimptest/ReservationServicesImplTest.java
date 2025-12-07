package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;

import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.exceptionhandling.ReservationNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.ServiceNotFoundException;

import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationServiceRepository;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesCreator;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesSaver;
import com.api.apicheck_incheck_out.service.impl.ReservationServicesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServicesImplTest {
    @Mock
    private ReservationServiceRepository reservationServiceRepository;

    @Mock
    private ReservationServicesFinder finder;

    @Mock
    private ReservationServicesCreator creator;

    @Mock
    private ReservationServicesSaver saver;
    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ReservationServicesServiceImpl reservationService;

    private ReservationServices reservationServices;


    @BeforeEach
    void setup(){

        reservationServices=new ReservationServices();
        reservationServices.setId(1L);
        reservationServices.setPhaseAjoutService(PhaseAjoutService.CHECK_IN);
    }
    @Test
    void testGetAllServicesByReservation(){
        List<ReservationServices> servicesList=Arrays.asList(reservationServices);
        when(reservationServiceRepository.findByReservationId(1L)).thenReturn(servicesList);

        List<ReservationServices> result=reservationService.getAllServicesByReservation(1L);

        assertEquals(1,result.size());
        verify(reservationServiceRepository,times(1)).findByReservationId(1L);
    }
    @Test
    void testGetServicesByPhase(){
        PhaseAjoutService phase=PhaseAjoutService.CHECK_IN;
        List<ReservationServices> serviceList= Arrays.asList(reservationServices);
        when(reservationServiceRepository.findByReservationAndPhase(1L,phase)).thenReturn(serviceList);

        List<ReservationServices> result=reservationService.getServicesByPhase(1L, phase);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reservationServices, result.get(0));

        verify(reservationServiceRepository, times(1)).findByReservationAndPhase(1L, phase);

    }
    @Test
    void testAddResService(){
        Long reservationId = 1L;
        List<Long> serviceIds = List.of(10L, 20L);

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);

        Services service1 = new Services();
        service1.setId(10L);
        Services service2 = new Services();
        service2.setId(20L);

        ReservationServices rs1 = new ReservationServices();
        ReservationServices rs2 = new ReservationServices();
        when(finder.findReservationById(1L)).thenReturn(reservation);
        when(creator.createReservationService(reservation, service1.getId(), PhaseAjoutService.CHECK_IN))
                .thenReturn(rs1);
        when(creator.createReservationService(reservation, service2.getId(), PhaseAjoutService.CHECK_IN))
                .thenReturn(rs2);

        List<ReservationServices> result = reservationService.addResService(reservationId, serviceIds);


        assertEquals(2, result.size());
        assertEquals(rs1, result.get(0));
        assertEquals(rs2, result.get(1));

        verify(reservationServiceRepository).saveAll(List.of(rs1, rs2));
    }

    @Test
    void testGetAvailableServices(){

        Services service1=new Services();
        service1.setId(1L);

        ReservationServices rsvServices=new ReservationServices();
        rsvServices.setService(service1);

        Reservation reservation=new Reservation();
        reservation.setId(1L);
        reservation.setServiceList(List.of(rsvServices));

        Services service2=new Services();
        service2.setId(2L);
        Services service3=new Services();
        service3.setId(3L);

        List<Services> allServices=List.of(service1,service2,service3);

        when(finder.findReservationById(1L)).thenReturn(reservation);
        when(serviceRepository.findAll()).thenReturn(allServices);

        List<Services> availableServices=reservationService.getAvailableServices(1L);

        assertEquals(2, availableServices.size());
        assertTrue(availableServices.contains(service2));
        assertTrue(availableServices.contains(service3));
        assertFalse(availableServices.contains(service1));
    }

    @Test
    void testGetRsvServicesSejourUnpaid(){
        Long reservationId=1L;

        Services service1=new Services();
        service1.setId(1L);
        service1.setNom("service 1");
        Services service2=new Services();
        service2.setId(2L);
        service2.setNom("service 2");

        ReservationServices rs1=new ReservationServices();
        rs1.setService(service1);
        ReservationServices rs2=new ReservationServices();
        rs2.setService(service2);

        when(reservationServiceRepository.getRsrvServicesSejourUnpaid(reservationId)).thenReturn(List.of(rs1,rs2));

        List<Services> resultat=reservationService.getRsrvServicesSejourUnpaid(reservationId);
        assertEquals(2,resultat.size());
        assertTrue(resultat.contains(service1));
        assertTrue(resultat.contains(service2));

        verify(reservationServiceRepository,times(1)).getRsrvServicesSejourUnpaid(reservationId);

    }
    @Test
    void testAddSejourServicesToReservation_Success() {
        Long reservationId = 1L;
        List<Long> serviceIds = List.of(1L, 2L);

        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setServiceList(new ArrayList<>());

        when(finder.findReservationById(reservationId)).thenReturn(reservation);

        List<ReservationServices> newServices=List.of(new ReservationServices());
        when(creator.createNewReservationServices(reservation,serviceIds)).thenReturn(newServices);

        reservationService.addSejourServicesToReservation(1L,serviceIds);

        verify(saver).saveNewServicesIfPresent(reservation,newServices);

    }


}
