package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.Impl.ChambreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChambreServiceImplTest {
    @Mock
    private ChambreRepository chambreRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ChambreServiceImpl chambreService;
    private Chambre chambre;
    private Reservation reservation;
    @BeforeEach
    void setUp(){
        chambre =new Chambre();
        chambre.setId(1L);
        chambre.setStatut(ChambreStatut.DISPONIBLE);

        reservation=new Reservation();
        reservation.setId(1L);
    }
    @Test
    void TestgetChambres(){
        when(chambreRepository.findAll()).thenReturn(List.of(chambre));
        assertEquals(1,chambreService.getChambres().size());
        verify(chambreRepository).findAll();
    }
    @Test
    void TestgetChambreStatut(){
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        ChambreStatut statut =chambreService.getChambreStatut(1L);
        assertEquals(ChambreStatut.DISPONIBLE,statut);
        verify(chambreRepository).findById(1L);
    }
    @Test
    void TestgetChambre(){
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        Chambre result=chambreService.getChambre(1L);
        assertEquals(chambre,result);
        verify(chambreRepository).findById(1L);
    }
    @Test
    void TestgetChambresByReservation(){
        when(chambreRepository.findByReservation_Id(1L)).thenReturn(List.of(chambre));

        assertEquals(1,chambreService.getChambresByReservation(1L).size());
        verify(chambreRepository).findByReservation_Id(1L);
    }
    @Test
    void TestgetChambresDisponibles(){
        when(chambreRepository.findByStatut(ChambreStatut.DISPONIBLE)).thenReturn(List.of(chambre));

        assertEquals(1,chambreService.getChambresDisponibles().size());
        verify(chambreRepository).findByStatut(ChambreStatut.DISPONIBLE);
    }
    @Test
    void TestSetChambreOccupee() {
        reservation.setStatus(ReservationStatus.Confirmee);

        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        chambreService.setChambreOccupee(1L,1L);

        assertEquals(ChambreStatut.OCCUPEE,chambre.getStatut());
        assertEquals(reservation, chambre.getReservation());
        verify(chambreRepository).save(chambre);
    }
    @Test
    void TestsetChambreDisponible(){
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        chambre.setReservation(null);
        chambreService.setChambreDisponible(1L);

        assertEquals(ChambreStatut.DISPONIBLE,chambre.getStatut());
        assertNull(chambre.getReservation());
        verify(chambreRepository).save(chambre);
    }
    @Test
    void TestsetChambreReserved() {
        reservation.setStatus(ReservationStatus.En_Attente);

        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        chambreService.setChambreReserved(1L,1L);

        assertEquals(ChambreStatut.RESERVED,chambre.getStatut());
        assertEquals(reservation, chambre.getReservation());
        verify(chambreRepository).save(chambre);
    }

}
