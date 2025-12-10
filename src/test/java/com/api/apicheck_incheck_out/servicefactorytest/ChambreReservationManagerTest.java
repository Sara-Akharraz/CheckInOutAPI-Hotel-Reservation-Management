package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.ReservationService;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChambreReservationManagerTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ChambreRepository chambreRepository;
    @Mock
    ChambreReservationRepository chambreReservationRepository;
    @InjectMocks
    ChambreReservationManager chambreReservationManager;
    @Mock
    ChambreMapper chambreMapper;

    @Test
    void testProcessAddReservation_Chambre(){
        Reservation reservation=new Reservation();
        reservation.setId(1L);
        List<Long> chambreIds=List.of(1L);
        Chambre chambre = Chambre.builder().id(1L).build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(chambreRepository.findById(1L)).thenReturn(Optional.ofNullable(chambre));
        Reservation result = chambreReservationManager.processChambreReservation(reservation, chambreIds);

        assertNotNull(result);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(chambreRepository, times(1)).findById(1L);

    }

    @Test
    void testProcessAddReservation_ChambreNotFound(){
        Reservation reservation=new Reservation();
        reservation.setId(1L);
        List<Long> chambreIds=List.of(1L);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(chambreRepository.findById(1L)).thenReturn(Optional.empty());
        ChambreNotFoundException ex = assertThrows(ChambreNotFoundException.class, () ->
                chambreReservationManager.processChambreReservation(reservation, chambreIds)
        );

        assertEquals("Chambre non trouvée dans la base de données : 1", ex.getMessage());

        verify(chambreReservationRepository, never()).save(any(ChambreReservation.class));
    }

    @Test
    void testFindRoomsOfReservation(){
        Reservation reservation=new Reservation();
        reservation.setId(1L);
        ChambreDTO chambreDTO = new ChambreDTO();
        chambreDTO.setId(1L);
        Chambre chambre = new Chambre();
        chambre.setId(1L);
        ChambreReservation cr = new ChambreReservation();
        cr.setReservation(reservation);
        cr.setChambre(chambre);
        List<ChambreReservation> crs = List.of(cr);
        reservation.setChambreReservations(crs);
        when(chambreMapper.toDTO(any(Chambre.class))).thenReturn(chambreDTO);
        List<ChambreDTO> result = chambreReservationManager.findRoomsOfReservation(reservation);
        assertNotNull(result);
        verify(chambreMapper, times(1)).toDTO(any(Chambre.class));

    }
}
