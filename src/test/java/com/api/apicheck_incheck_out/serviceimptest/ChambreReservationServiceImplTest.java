package com.api.apicheck_incheck_out.serviceimptest;


import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.repository.ChambreReservationRepository;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationFilter;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationFinder;
import com.api.apicheck_incheck_out.service.factory.ChambreReservationStatusManager;
import com.api.apicheck_incheck_out.service.impl.ChambreReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChambreReservationServiceImplTest {

    @Mock
    private ChambreReservationRepository chambreReservationRepository;

    @Mock
    private ChambreReservationStatusManager manager;

    @Mock
    private ChambreReservationFinder finder;

    @Mock
    private ChambreReservationFilter filter;

    @InjectMocks
    ChambreReservationServiceImpl chambreReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChambreStatut_Success() {
        Long reservationId = 1L;
        Long chambreId = 2L;

        ChambreReservation cr = new ChambreReservation();
        cr.setStatut(ChambreStatut.DISPONIBLE);

        when(finder.findChambreReservation(reservationId, chambreId))
                .thenReturn(cr);

        ChambreStatut statut = chambreReservationService.getChambreStatut(reservationId, chambreId);

        assertEquals(ChambreStatut.DISPONIBLE, statut);
    }

    @Test
    void testGetChambresByReservation(){
        Reservation res=new Reservation();
        res.setId(100L);
        Chambre c1= new Chambre();
        c1.setId(10L);
        Chambre c2=new Chambre();
        c2.setId(20L);
        ChambreReservation cr1 = new ChambreReservation();
        cr1.setChambre(c1);
        ChambreReservation cr2 = new ChambreReservation();
        cr2.setChambre(c2);

        when(finder.findChambreReservationByReservationId(100L)).thenReturn(List.of(cr1,cr2));

        List<Chambre> chambres = chambreReservationService.getChambresByReservation(100L);

        assertEquals(2,chambres.size());
        assertEquals(10L,chambres.get(0).getId());
        assertEquals(20L,chambres.get(1).getId());

    }
    @Test
    void testGetChambresDisponibles(){
        Reservation res=new Reservation();
        res.setId(100L);
        Chambre c1= new Chambre();
        c1.setId(10L);
        Chambre c2=new Chambre();
        c2.setId(20L);
        ChambreReservation cr1 = new ChambreReservation();
        cr1.setChambre(c1);
        ChambreReservation cr2 = new ChambreReservation();
        cr2.setChambre(c2);

        when(chambreReservationRepository.findByStatut(ChambreStatut.DISPONIBLE)).thenReturn(List.of(cr1,cr2));

        List<Chambre> chambres = chambreReservationService.getChambresDisponibles();

        assertEquals(2,chambres.size());
        assertEquals(10L,chambres.get(0).getId());
        assertEquals(20L,chambres.get(1).getId());

        verify(chambreReservationRepository,times(1)).findByStatut(ChambreStatut.DISPONIBLE);

    }
    @Test
    void testSetChambreOccupee(){
        chambreReservationService.setChambreOccupee(1L);
        verify(manager).setChambreOccupee(1L);
    }
    @Test
    void testSetChambreDisponible(){
        chambreReservationService.setChambreDisponible(1L);
        verify(manager).setChambreDisponible(1L);
    }
    @Test
    void testSetChambreReserved(){
        chambreReservationService.setChambreReserved(10L,1L);
        verify(manager).setChambreReserved(10L,1L);
    }
    @Test
    void testFindChambreDisponibles(){
        List<Chambre> expected=List.of(new Chambre());
        when(filter.findChambresDisponibles( "2025-12-01", "2025-12-05", 2, ChambreType.SINGLE, "1")).thenReturn(expected);
        List<Chambre> result=chambreReservationService.findChambresDisponibles("2025-12-01", "2025-12-05", 2, ChambreType.SINGLE, "1");

        assertSame(expected,result);
    }
}
