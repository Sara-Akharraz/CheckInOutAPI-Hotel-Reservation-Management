package com.api.apicheck_incheck_out.dtotest;

import com.api.apicheck_incheck_out.dto.CheckInDTO;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

 class CheckInDTOTest {
    @Test
    void testCheckInDTO(){
        //allArgsConstructor
        CheckInDTO dto1=new CheckInDTO(1L, LocalDate.now(), CheckInStatus.EN_ATTENTE,1L,1L);

        //setters
        CheckInDTO dto2=new CheckInDTO();
        dto2.setId(1L);
        dto2.setDateCheckIn(LocalDate.now());
        dto2.setIdDocumentScan(1L);
        dto2.setIdReservation(1L);
        dto2.setStatus(CheckInStatus.EN_ATTENTE);

        //getters
        assertEquals(1L,dto2.getId());
        assertEquals(1L,dto2.getIdReservation());
        assertEquals(1L,dto2.getIdDocumentScan());
        assertEquals(CheckInStatus.EN_ATTENTE,dto2.getStatus());
        assertEquals(LocalDate.now(),dto2.getDateCheckIn());

        //equals
        assertEquals(dto1,dto2);
        CheckInDTO dto3=new CheckInDTO(1L, LocalDate.now(), CheckInStatus.VALIDE,1L,1L);
        assertNotEquals(dto1,dto3);

        //ToString
        assertNotNull(dto1.toString());

        //hashCode
        assertEquals(dto1.hashCode(),dto2.hashCode());
        assertNotEquals(dto1.hashCode(),dto3.hashCode());
    }
}
