package com.api.apicheck_incheck_out.dtotest;

import com.api.apicheck_incheck_out.dto.CheckOutDTO;
import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckOutDTOTest {
    @Test
    void testCheckOutDTO(){

        //builder
        CheckOutDTO checkOut1=CheckOutDTO.builder()
                .id(1L)
                .idReservation(1L)
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .build();
        CheckOutDTO checkOut2=CheckOutDTO.builder()
                .id(1L)
                .idReservation(1L)
                .checkOutStatut(CheckOutStatut.EN_ATTENTE)
                .build();
        CheckOutDTO checkOut3=CheckOutDTO.builder()
                .id(3L)
                .idReservation(1L)
                .checkOutStatut(CheckOutStatut.CONFIRMEE)
                .build();
        //getters
        assertEquals(1L,checkOut1.getId());
        assertEquals(1L,checkOut1.getIdReservation());
        assertEquals(CheckOutStatut.EN_ATTENTE,checkOut1.getCheckOutStatut());

        //equals
        assertEquals(checkOut1,checkOut2);
        assertNotEquals(checkOut1,checkOut3);
        //hashCode
        assertEquals(checkOut1.hashCode(),checkOut2.hashCode());
        assertNotEquals(checkOut1.hashCode(),checkOut3.hashCode());
        //ToString
        assertNotNull(checkOut1.toString());
        //setters
        checkOut1.setId(10L);
        checkOut1.setIdReservation(200L);
        checkOut1.setCheckOutStatut(CheckOutStatut.CONFIRMEE);
    }
}
