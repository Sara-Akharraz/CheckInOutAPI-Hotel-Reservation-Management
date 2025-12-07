package com.api.apicheck_incheck_out.servicefactory;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.exceptionhandling.ServiceNotFoundException;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesCreator;
import com.api.apicheck_incheck_out.service.factory.ReservationServicesFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ReservationServicesCreatorTest {
    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ReservationServicesFinder reservationServicesFinder;

    @InjectMocks
    private ReservationServicesCreator creator;

    @Test
    void testCreateReservationService_ServiceExists() {

        Reservation reservation = new Reservation();

        Services service = new Services();
        service.setId(5L);

        when(serviceRepository.findById(5L)).thenReturn(Optional.of(service));

        ReservationServices rs =creator.createReservationService(reservation, 5L, PhaseAjoutService.CHECK_IN);

        assertNotNull(rs);
        assertEquals(reservation, rs.getReservation());
        assertEquals(service, rs.getService());
        assertEquals(PaiementStatus.EN_ATTENTE, rs.getPaiementStatus());
        assertEquals(PhaseAjoutService.CHECK_IN, rs.getPhaseAjoutService());
    }
    @Test
    void testCreateReservationService_ServiceNotFound() {

        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> {
            creator.createReservationService(new Reservation(), 1L, PhaseAjoutService.CHECK_IN);
        });
    }
    @Test
    void testCreateNewReservationServices_FilterExistingServices() {

        Reservation reservation = new Reservation();

        when(reservationServicesFinder.extractExistingServiceIds(reservation))
                .thenReturn(Set.of(1L, 2L));

        List<Long> serviceIds = List.of(1L, 2L, 3L, 4L);

        Services service3 = new Services();
        service3.setId(3L);

        Services service4 = new Services();
        service4.setId(4L);

        when(serviceRepository.findById(3L)).thenReturn(Optional.of(service3));
        when(serviceRepository.findById(4L)).thenReturn(Optional.of(service4));

        List<ReservationServices> result =
                creator.createNewReservationServices(reservation, serviceIds);

        assertEquals(2, result.size());

        List<Long> returnedIds =
                result.stream().map(rs -> rs.getService().getId()).toList();

        assertTrue(returnedIds.contains(3L));
        assertTrue(returnedIds.contains(4L));

        assertTrue(
                result.stream()
                        .allMatch(rs -> rs.getPhaseAjoutService() == PhaseAjoutService.SEJOUR)
        );
    }
    @Test
    void testCreateNewReservationServices() {

        Reservation reservation = new Reservation();
        when(reservationServicesFinder.extractExistingServiceIds(reservation))
                .thenReturn(Set.of(1L, 2L));

        List<ReservationServices> result =
                creator.createNewReservationServices(reservation, List.of(1L, 2L));

        assertTrue(result.isEmpty());
    }
}
