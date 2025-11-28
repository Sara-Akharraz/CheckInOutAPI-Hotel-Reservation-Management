package com.api.apicheck_incheck_out.entitytest;

import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.entity.ChambreReservation;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.ChambreStatut;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class ChamrbeReservationTest {
    @Test
    void testChambreReservation() {
        Reservation res1 = new Reservation();
        res1.setId(1L);

        Chambre chambre1 = new Chambre();
        chambre1.setId(10L);

        ChambreReservation cr1 = new ChambreReservation(100L, res1, chambre1, ChambreStatut.DISPONIBLE);

        assertEquals(100L, cr1.getId());
        assertEquals(res1, cr1.getReservation());
        assertEquals(chambre1, cr1.getChambre());
        assertEquals(ChambreStatut.DISPONIBLE, cr1.getStatut());

        cr1.setId(200L);
        assertEquals(200L, cr1.getId());

        ChambreReservation cr2 = new ChambreReservation(200L, res1, chambre1, ChambreStatut.DISPONIBLE);
        ChambreReservation cr3 = new ChambreReservation(300L, res1, chambre1, ChambreStatut.RESERVED);

        assertEquals(cr1, cr2);
        assertNotEquals(cr1, cr3);
        assertEquals(cr1.hashCode(), cr2.hashCode());
        assertNotEquals(cr1.hashCode(), cr3.hashCode());


        String str = cr1.toString();
        assertTrue(str.contains("DISPONIBLE") || str.contains("200"));
    }
}
