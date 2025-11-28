package com.api.apicheck_incheck_out.mappertest;


import com.api.apicheck_incheck_out.dto.ReservationDTO;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.ReservationStatus;
import com.api.apicheck_incheck_out.mapper.ReservationMapper;
import com.api.apicheck_incheck_out.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


class ReservationMapperTest {

    private UserRepository userRepository;
    private FactureRepository factureRepository;
    private CheckInRepository checkInRepository;
    private CheckOutRepository checkOutRepository;
    private ReservationServiceRepository reservationServiceRepository;
    private ChambreReservationRepository chambreReservationRepository;

    private ReservationMapper mapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        factureRepository = mock(FactureRepository.class);
        checkInRepository = mock(CheckInRepository.class);
        checkOutRepository = mock(CheckOutRepository.class);
        reservationServiceRepository = mock(ReservationServiceRepository.class);
        chambreReservationRepository = mock(ChambreReservationRepository.class);

        mapper = new ReservationMapper(
                userRepository,
                factureRepository,
                checkInRepository,
                checkOutRepository,
                reservationServiceRepository,
                chambreReservationRepository
        );
    }

    @Test
    void toDTO_shouldMapEntityToDTO() {
        User user = new User();
        user.setId(1L);

        CheckIn checkIn = new CheckIn();
        checkIn.setId(100L);

        CheckOut checkOut = new CheckOut();
        checkOut.setId(200L);

        Facture facture1 = new Facture();
        facture1.setId(10L);
        Facture facture2 = new Facture();
        facture2.setId(11L);

        ReservationServices service1 = new ReservationServices();
        service1.setId(20L);

        ChambreReservation chambre1 = new ChambreReservation();
        chambre1.setId(30L);

        Reservation reservation = new Reservation(
                1L,
                user,
                List.of(chambre1),
                LocalDate.of(2025, 11, 28),
                LocalDate.of(2025, 11, 30),
                ReservationStatus.EN_ATTENTE,
                List.of(facture1, facture2),
                checkIn,
                checkOut,
                List.of(service1)
        );

        ReservationDTO dto = mapper.toDTO(reservation);

        assertNotNull(dto);
        assertEquals(reservation.getId(), dto.getId());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(checkIn.getId(), dto.getCheckinId());
        assertEquals(checkOut.getId(), dto.getCheckoutId());
        assertEquals(1, dto.getChambreList().size());
        assertEquals(2, dto.getFactureList().size());
        assertEquals(1, dto.getServices().size());
    }

    @Test
    void toDTO_shouldMapEntityToDTOWithNullCheckInAndCheckOut() {
        User user = new User();
        user.setId(1L);

        CheckIn checkIn = null;

        CheckOut checkOut = null;

        Facture facture1 = new Facture();
        facture1.setId(10L);
        Facture facture2 = new Facture();
        facture2.setId(11L);

        ReservationServices service1 = new ReservationServices();
        service1.setId(20L);

        ChambreReservation chambre1 = new ChambreReservation();
        chambre1.setId(30L);

        Reservation reservation = new Reservation(
                1L,
                user,
                List.of(chambre1),
                LocalDate.of(2025, 11, 28),
                LocalDate.of(2025, 11, 30),
                ReservationStatus.EN_ATTENTE,
                List.of(facture1, facture2),
                checkIn,
                checkOut,
                List.of(service1)
        );

        ReservationDTO dto = mapper.toDTO(reservation);

        assertNotNull(dto);
        assertEquals(reservation.getId(), dto.getId());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(reservation.getCheckIn(), null);
        assertEquals(reservation.getCheckOut(), null);
        assertEquals(1, dto.getChambreList().size());
        assertEquals(2, dto.getFactureList().size());
        assertEquals(1, dto.getServices().size());
    }

    @Test
    void toEntity_shouldMapDTOToEntity() {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setChambreList(List.of(30L));
        dto.setFactureList(List.of(10L, 11L));
        dto.setServices(List.of(20L));
        dto.setCheckinId(100L);
        dto.setCheckoutId(200L);

        User user = new User();
        user.setId(1L);

        CheckIn checkIn = new CheckIn();
        checkIn.setId(100L);

        CheckOut checkOut = new CheckOut();
        checkOut.setId(200L);

        Facture facture1 = new Facture();
        facture1.setId(10L);
        Facture facture2 = new Facture();
        facture2.setId(11L);

        ReservationServices service1 = new ReservationServices();
        service1.setId(20L);

        ChambreReservation chambre1 = new ChambreReservation();
        chambre1.setId(30L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(checkInRepository.findById(100L)).thenReturn(Optional.of(checkIn));
        when(checkOutRepository.findById(200L)).thenReturn(Optional.of(checkOut));
        when(factureRepository.findAllById(dto.getFactureList())).thenReturn(List.of(facture1, facture2));
        when(reservationServiceRepository.findAllById(dto.getServices())).thenReturn(List.of(service1));
        when(chambreReservationRepository.findAllById(dto.getChambreList())).thenReturn(List.of(chambre1));

        Reservation reservation = mapper.toEntity(dto);

        assertNotNull(reservation);
        assertEquals(dto.getId(), reservation.getId());
        assertEquals(user, reservation.getUser());
        assertEquals(1, reservation.getChambreReservations().size());
        assertEquals(2, reservation.getFactureList().size());
        assertEquals(1, reservation.getServiceList().size());
        assertEquals(checkIn, reservation.getCheckIn());
        assertEquals(checkOut, reservation.getCheckOut());
    }

    @Test
    void toEntity_shouldThrow_whenUserNotFound() {
        ReservationDTO dto = new ReservationDTO();
        dto.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> mapper.toEntity(dto));
        assertEquals("User not found with ID: 999", ex.getMessage());
    }

    @Test
    void toEntity_shouldHandleNullAndEmptyLists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ReservationDTO dto = new ReservationDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setChambreList(null);
        dto.setFactureList(List.of());
        dto.setServices(null);
        dto.setCheckinId(null);
        dto.setCheckoutId(null);

        Reservation reservation = mapper.toEntity(dto);

        assertEquals(user, reservation.getUser());
        assertNotNull(reservation.getChambreReservations());
        assertTrue(reservation.getChambreReservations().isEmpty());

        assertNotNull(reservation.getFactureList());
        assertTrue(reservation.getFactureList().isEmpty());


        assertNotNull(reservation.getServiceList());
        assertTrue(reservation.getServiceList().isEmpty());

        assertNull(reservation.getCheckIn());
        assertNull(reservation.getCheckOut());
    }
}