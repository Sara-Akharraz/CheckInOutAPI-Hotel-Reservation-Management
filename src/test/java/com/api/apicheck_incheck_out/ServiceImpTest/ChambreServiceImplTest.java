package com.api.apicheck_incheck_out.ServiceImpTest;

import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ChambreType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        chambre.setNom("Chambre 101");
        chambre.setPrix(500.0);
        reservation=new Reservation();
        reservation.setId(1L);
    }

    @Test
    void TestAddChambre(){
        when(chambreRepository.save(chambre)).thenReturn(chambre);
        Chambre createdChambre=chambreService.addChambre(chambre);

        assertNotNull(createdChambre);
        assertEquals("Chambre 101",createdChambre.getNom());
        assertEquals(500.0,createdChambre.getPrix());

        verify(chambreRepository,times(1)).save(chambre);
    }
    @Test
    void TestupdateChmabre(){
        Chambre updated=new Chambre();
        updated.setNom("Chambre 102");
        updated.setPrix(200.0);
        updated.setStatut(ChambreStatut.DISPONIBLE);
        updated.setType(ChambreType.Double);
        updated.setReservation(reservation);

        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        when(chambreRepository.save(any(Chambre.class))).thenReturn(updated);

        Chambre result=chambreService.updateChambre(1L,updated);

        assertNotNull(result);
        assertEquals("Chambre 102",result.getNom());
        assertEquals(200.0,result.getPrix());
        assertEquals(ChambreStatut.DISPONIBLE,result.getStatut());

        verify(chambreRepository,times(1)).findById(1L);
        verify(chambreRepository,times(1)).save(chambre);

    }
    @Test
    void TestdeleteChambre(){
        when(chambreRepository.existsById(1L)).thenReturn(true);
        doNothing().when(chambreRepository).deleteById(1L);

        chambreService.deleteChambre(1L);

        verify(chambreRepository,times(1)).existsById(1L);
        verify(chambreRepository,times(1)).deleteById(1L);
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
