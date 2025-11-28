package com.api.apicheck_incheck_out.mappertest;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import com.api.apicheck_incheck_out.exceptionhandling.CheckInNotFoundException;
import com.api.apicheck_incheck_out.mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class DocuementScanMapperTest {
    @InjectMocks
    DocumentScanMapper documentScanMapper;
    @Mock
    CheckInRepository checkInRepository;
    @Test
    void testToDTO(){
        DocumentScan doc = new DocumentScan();
        doc.setId(1L);
        doc.setNom("Sara");
        doc.setPrenom("Akharraz");
        doc.setCin("XX123456");
        doc.setType(DocumentScanType.CIN);

        DocumentScanDTO dto = documentScanMapper.toDTO(doc);

        assertEquals(1L, dto.getId());
        assertEquals("Sara", dto.getNom());
        assertEquals("Akharraz", dto.getPrenom());
        assertEquals("XX123456", dto.getCin());
        assertEquals(DocumentScanType.CIN, dto.getType());
    }
    @Test
    void testToEntity_CheckIn(){
        DocumentScanDTO dto = new DocumentScanDTO(
                1L, "Sara", "Akharraz", DocumentScanType.CIN,"XX123456", null, null, null, null
        );

        Reservation res = new Reservation();
        res.setId(99L);

        CheckIn checkIn = CheckIn.builder()
                .id(1L)
                .reservation(res)
                .build();

        when(checkInRepository.findById(1L)).thenReturn(Optional.of(checkIn));

        DocumentScan entity = documentScanMapper.toEntity(dto, 1L);

        assertEquals(1L, entity.getId());
        assertEquals("Sara", entity.getNom());
        assertEquals(checkIn, entity.getCheckIn());
        assertEquals(entity, checkIn.getDocumentScan());
    }
    @Test
    void testToEntity_CheckInNull(){
        DocumentScanDTO dto = new DocumentScanDTO(
                1L, "Sara", "Akharraz", DocumentScanType.CIN, "XX123456", null, null, null, null
        );

        DocumentScan entity = documentScanMapper.toEntity(dto, null);

        assertNull(entity.getCheckIn());
    }
    @Test
    void testToEntity_ThrowsCheckInNotFoundException(){
        Long checkInId=1L;
        DocumentScanDTO dto = new DocumentScanDTO(
                1L, "Sara", "Akharraz", DocumentScanType.CIN, "XX123456", null, null, null, null
        );
        when(checkInRepository.findById(checkInId)).thenReturn(Optional.empty());
        CheckInNotFoundException ex=assertThrows(CheckInNotFoundException.class,()->documentScanMapper.toEntity(dto,checkInId));
        assertEquals("Check_in non trouvé",ex.getMessage());
        verify(checkInRepository,times(1)).findById(checkInId);

    }
}
