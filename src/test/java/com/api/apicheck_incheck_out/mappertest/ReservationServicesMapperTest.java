package com.api.apicheck_incheck_out.mappertest;


import com.api.apicheck_incheck_out.dto.ReservationServicesDTO;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.ReservationServices;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.PaiementStatus;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.mapper.ReservationServicesMapper;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ReservationServicesMapperTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ReservationServicesMapper mapper;

    @Test
    void toDTO_shouldMapEntityToDTO() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        Services service = new Services();
        service.setId(2L);

        ReservationServices entity = new ReservationServices();
        entity.setId(10L);
        entity.setReservation(reservation);
        entity.setService(service);
        entity.setPhaseAjoutService(PhaseAjoutService.SEJOUR);
        entity.setPaiementStatus(PaiementStatus.PAYE);

        ReservationServicesDTO dto = mapper.toDTO(entity);

        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getIdReservation());
        assertEquals(2L, dto.getIdService());
        assertEquals(PhaseAjoutService.SEJOUR, dto.getPhaseAjoutService());
        assertEquals(PaiementStatus.PAYE, dto.getPaiementStatus());
    }

    @Test
    void toEntity_shouldMapDTOToEntity_whenEntitiesExist() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        Services service = new Services();
        service.setId(2L);

        ReservationServicesDTO dto = new ReservationServicesDTO();
        dto.setId(10L);
        dto.setIdReservation(1L);
        dto.setIdService(2L);
        dto.setPhaseAjoutService(PhaseAjoutService.SEJOUR);
        dto.setPaiementStatus(PaiementStatus.PAYE);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(serviceRepository.findById(2L)).thenReturn(Optional.of(service));

        ReservationServices entity = mapper.toEntity(dto);

        assertEquals(10L, entity.getId());
        assertEquals(reservation, entity.getReservation());
        assertEquals(service, entity.getService());
        assertEquals(PhaseAjoutService.SEJOUR, entity.getPhaseAjoutService());
        assertEquals(PaiementStatus.PAYE, entity.getPaiementStatus());
    }

    @Test
    void toEntity_shouldThrow_whenReservationNotFound() {
        ReservationServicesDTO dto = new ReservationServicesDTO();
        dto.setIdReservation(1L);
        dto.setIdService(2L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mapper.toEntity(dto));
        assertTrue(exception.getMessage().contains("Reservation non trouvée"));
    }

    @Test
    void toEntity_shouldThrow_whenServiceNotFound() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        ReservationServicesDTO dto = new ReservationServicesDTO();
        dto.setIdReservation(1L);
        dto.setIdService(2L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(serviceRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mapper.toEntity(dto));
        assertTrue(exception.getMessage().contains("Service non trouvé"));
    }
}
