package com.api.apicheck_incheck_out.mappertest;

import com.api.apicheck_incheck_out.dto.CheckInDTO;
import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.mapper.CheckInMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
 class CheckInMapperTest {
    @Mock
    CheckInMapper checkInMapper;
    @BeforeEach
    void setUp() {
        checkInMapper = new CheckInMapper();
    }
    @Test
    void testToDTO(){
        Reservation res=new Reservation();
        res.setId(1L);
        DocumentScan doc=new DocumentScan();
        doc.setId(1L);
        CheckIn checkIn=CheckIn.builder()
                .id(1L)
                .dateCheckIn(LocalDate.of(2025,11,28))
                .status(CheckInStatus.EN_ATTENTE)
                .reservation(res)
                .documentScan(doc)
                .build();

        CheckInDTO dto=checkInMapper.toDTO(checkIn);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(LocalDate.of(2025,11,28), dto.getDateCheckIn());
        assertEquals(CheckInStatus.EN_ATTENTE, dto.getStatus());
        assertEquals(1L, dto.getIdReservation());
        assertEquals(1L, dto.getIdDocumentScan());
    }
    @Test
    void testToDTO_NullInput() {
        assertNull(checkInMapper.toDTO(null));
    }
    @Test
    void testToDTO_NullReservationNullDocumentScan() {
        CheckIn checkIn = new CheckIn();
        checkIn.setId(1L);
        checkIn.setDateCheckIn(LocalDate.now());
        checkIn.setStatus(CheckInStatus.EN_ATTENTE);
        checkIn.setReservation(null);
        checkIn.setDocumentScan(null);

        CheckInDTO dto = checkInMapper.toDTO(checkIn);

        assertNotNull(dto);
        assertNull(dto.getIdReservation());
        assertNull(dto.getIdDocumentScan());
    }

}
