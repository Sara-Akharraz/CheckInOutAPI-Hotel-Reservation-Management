package com.api.apicheck_incheck_out.servicefactorytest;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import com.api.apicheck_incheck_out.repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.service.factory.DocumentScanFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class DocumentScanFactoryTest {
    @Mock
    private DocumentScanRepository documentScanRepository;
    @InjectMocks
    private DocumentScanFactory factory;
    @Test
    void testCreateAndSave() {

        DocumentScanDTO dto = new DocumentScanDTO();
        dto.setNom("akharraz");
        dto.setPrenom("sara");
        dto.setCin("AB123456");
        dto.setPassport("P1234567");
        dto.setType(DocumentScanType.CIN);
        dto.setImage(new byte[]{1,2,3});
        dto.setFileName("doc.pdf");
        dto.setFileType("application/pdf");

        DocumentScan saved = new DocumentScan();
        saved.setNom(dto.getNom());
        saved.setPrenom(dto.getPrenom());
        saved.setCin(dto.getCin());
        saved.setPassport(dto.getPassport());
        saved.setType(dto.getType());
        saved.setImage(dto.getImage());
        saved.setFileName(dto.getFileName());
        saved.setFileType(dto.getFileType());

        when(documentScanRepository.save(any(DocumentScan.class))).thenReturn(saved);

        DocumentScan result = factory.createAndSave(dto);

        assertNotNull(result);
        assertEquals(dto.getNom(), result.getNom());
        assertEquals(dto.getPrenom(), result.getPrenom());
        assertEquals(dto.getCin(), result.getCin());
        assertEquals(dto.getPassport(), result.getPassport());
        assertEquals(dto.getType(), result.getType());
        assertArrayEquals(dto.getImage(), result.getImage());
        assertEquals(dto.getFileName(), result.getFileName());
        assertEquals(dto.getFileType(), result.getFileType());

        verify(documentScanRepository, times(1)).save(any(DocumentScan.class));
    }
}
