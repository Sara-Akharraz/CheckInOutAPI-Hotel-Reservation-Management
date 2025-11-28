package com.api.apicheck_incheck_out.entitytest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.enums.ChambreType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChambreTest {
    @Test
    void testChambreBuilderAndAllArgsConstructor() {

        Chambre c1 = new Chambre(1L,"chambre 104","1",100.0, ChambreType.DOUBLE,3,new ArrayList<>());

        assertEquals(1L, c1.getId());
        assertEquals("chambre 104", c1.getNom());
        assertEquals(3, c1.getCapacite());

        Chambre c2 = Chambre.builder()
                .id(2L)
                .nom("chambre 104")
                .prix(100.0)
                .etage("1")
                .capacite(3)
                .type(ChambreType.DOUBLE)
                .build();

        assertEquals(2L, c2.getId());
        assertEquals("chambre 104", c2.getNom());
        assertEquals(3, c2.getCapacite());
    }
    @Test
     void testChambre_Equals_HashCode_ToString(){
        Chambre c1 = Chambre.builder()
                .id(2L)
                .nom("chambre 104")
                .prix(100.0)
                .etage("1")
                .capacite(3)
                .type(ChambreType.DOUBLE)
                .build();
        Chambre c2 = Chambre.builder()
                .id(2L)
                .nom("chambre 104")
                .prix(100.0)
                .etage("1")
                .capacite(3)
                .type(ChambreType.DOUBLE)
                .build();
        Chambre c3 = Chambre.builder()
                .id(2L)
                .nom("chambre 104")
                .prix(300.0)
                .etage("1")
                .capacite(3)
                .type(ChambreType.DOUBLE)
                .build();

        assertEquals(c1,c2);
        assertNotEquals(c1,c3);
        assertTrue(c1.equals(c2));

        assertEquals(c1.hashCode(),c2.hashCode());
        assertNotEquals(c1.hashCode(),c3.hashCode());

        String s=c1.toString();
        assertTrue(s.contains("chambre"));
        assertTrue(s.contains("3"));
    }
    @Test
    void testSetChambreReservation(){
        Chambre c1 = Chambre.builder()
                .id(2L)
                .nom("chambre 104")
                .prix(100.0)
                .etage("1")
                .capacite(3)
                .type(ChambreType.DOUBLE)
                .build();

        List<ChambreReservation> chambreReservationList= new ArrayList<>();
        ChambreReservation cr1=new ChambreReservation();
        cr1.setId(1L);
        ChambreReservation cr2=new ChambreReservation();
        cr2.setId(1L);
        chambreReservationList.add(cr1);
        chambreReservationList.add(cr2);

        c1.setChambreReservations(chambreReservationList);

        assertNotNull(c1.getChambreReservations());
        assertEquals(2, c1.getChambreReservations().size());
        assertEquals(cr1, c1.getChambreReservations().get(0));
        assertEquals(cr1, c1.getChambreReservations().get(1));
    }

}
