package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import com.api.apicheck_incheck_out.repository.ChambreRepository;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.service.impl.ChambreServiceImpl;
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
 class ChambreServiceImplTest {
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
        Chambre existed=new Chambre();
        existed.setNom("old name");
        existed.setPrix(100.0);

        Chambre updated=new Chambre();
        updated.setNom("Chambre 102");
        updated.setPrix(200.0);
        updated.setType(ChambreType.DOUBLE);

        when(chambreRepository.findById(1L)).thenReturn(Optional.of(existed));
        when(chambreRepository.save(any(Chambre.class))).thenReturn(updated);

        Chambre result=chambreService.updateChambre(1L,updated);

        assertNotNull(result);
        assertEquals("Chambre 102",result.getNom());
        assertEquals(200.0,result.getPrix());


        verify(chambreRepository,times(1)).findById(1L);
        verify(chambreRepository,times(1)).save(existed);



    }
    @Test
    void TestupdateChmabreThrowsException(){
        Chambre updated=new Chambre();
        updated.setNom("Chambre 102");
        updated.setPrix(200.0);
        updated.setType(ChambreType.DOUBLE);

        when(chambreRepository.findById(2L)).thenReturn(Optional.empty());
        ChambreNotFoundException ex=assertThrows(ChambreNotFoundException.class,()->chambreService.updateChambre(2L,updated));

        assertEquals("Chambre non trouvée avec l'id :2",ex.getMessage());

        verify(chambreRepository,times(1)).findById(2L);
        verify(chambreRepository,never()).save(any(Chambre.class));
    }
    @Test
    void TestdeleteChambre(){
        when(chambreRepository.existsById(1L)).thenReturn(true);

        chambreService.deleteChambre(1L);

        verify(chambreRepository,times(1)).existsById(1L);
        verify(chambreRepository,times(1)).deleteById(1L);

        when(chambreRepository.existsById(2L)).thenReturn(false);
        ChambreNotFoundException ex=assertThrows(ChambreNotFoundException.class,()->chambreService.deleteChambre(2L));

        assertEquals("Chambre non trouvée avec l'id :2",ex.getMessage());

        verify(chambreRepository,times(1)).existsById(2L);
        verify(chambreRepository,never()).deleteById(2L);
    }
    @Test
    void TestgetChambres(){
        when(chambreRepository.findAll()).thenReturn(List.of(chambre));
        assertEquals(1,chambreService.getChambres().size());
        verify(chambreRepository).findAll();
    }

    @Test
    void TestgetChambre(){
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));
        Chambre result=chambreService.getChambre(1L);
        assertEquals(chambre,result);
        verify(chambreRepository).findById(1L);
    }
    @Test
    void TestgetChambreThrowsException(){
        when(chambreRepository.findById(2L)).thenReturn(Optional.empty());

        ChambreNotFoundException ex=assertThrows(ChambreNotFoundException.class,()->chambreService.getChambre(2L));

        assertEquals("Chambre non trouvée avec l'id : 2", ex.getMessage());
        verify(chambreRepository,times(1)).findById(2L);
    }


}
