package com.api.apicheck_incheck_out.dtotest;

import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.enums.ChambreType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class ChambreDTOTest {
    @Test
    void testChambreDTO(){
        ChambreDTO dto1 = new ChambreDTO();
        dto1.setId(1L);
        dto1.setNom("Chambre 1");
        dto1.setEtage("1");
        dto1.setPrix(150.0);
        dto1.setType(ChambreType.DOUBLE);
        dto1.setCapacite(2);
        dto1.setChambreReservationIds(List.of(10L, 20L));

        // getters
        assertEquals(1L, dto1.getId());
        assertEquals("Chambre 1", dto1.getNom());
        assertEquals("1", dto1.getEtage());
        assertEquals(150.0, dto1.getPrix());
        assertEquals(ChambreType.DOUBLE, dto1.getType());
        assertEquals(2, dto1.getCapacite());
        assertEquals(List.of(10L, 20L), dto1.getChambreReservationIds());

        // toString
        assertNotNull(dto1.toString());

        //equals & hashCode
        ChambreDTO dto2 = new ChambreDTO(1L, "Chambre 1", "1", 150.0, ChambreType.DOUBLE, 2, List.of(10L, 20L));
        ChambreDTO dto3 = new ChambreDTO(2L, "Chambre 2", "2", 200.0, ChambreType.SINGLE, 4, List.of(30L));

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());

        //setters
        dto3.setNom("Chambre 1");
        dto3.setId(1L);
        dto3.setEtage("1");
        dto3.setPrix(150.0);
        dto3.setType(ChambreType.DOUBLE);
        dto3.setCapacite(2);
        dto3.setChambreReservationIds(List.of(10L, 20L));
        assertEquals(dto1, dto3);
    }
}
