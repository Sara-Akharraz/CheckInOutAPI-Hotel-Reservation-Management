package com.api.apicheck_incheck_out.controllertest;

import com.api.apicheck_incheck_out.controller.DocumentScanController;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.repository.DocumentScanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class DocumentScanControllerTest {
    @Mock
    DocumentScanRepository documentScanRepository;
    @InjectMocks
    DocumentScanController documentScanController;

    @Test
    void testPreviewDocument_success(){
        Long documentId=1L;
        DocumentScan doc=new DocumentScan();
        doc.setFileType("image/png");
        doc.setImage(new byte[]{1,2,3});

        when(documentScanRepository.findById(documentId)).thenReturn(Optional.of(doc));

        ResponseEntity<byte[]> response=documentScanController.previewDocument(documentId);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertArrayEquals(doc.getImage(),response.getBody());
        assertEquals(MediaType.IMAGE_PNG,response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getCacheControl().contains("max-age=3600"));
    }
    @Test
    void testPreviewDocument_DocumentNotFoundException(){
        Long id=1L;
        when(documentScanRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex=assertThrows(RuntimeException.class,()->documentScanController.previewDocument(id));
        assertEquals("Document non trouvé",ex.getMessage());
    }
    @Test
    void testPreviewDocument_UnsupportedOperationException(){
        Long documentId=1L;
        DocumentScan doc=new DocumentScan();
        doc.setFileType("application/pdf");
        doc.setImage(new byte[]{1,2,3});

        when(documentScanRepository.findById(documentId)).thenReturn(Optional.of(doc));

        UnsupportedOperationException ex=assertThrows(UnsupportedOperationException.class,()->documentScanController.previewDocument(documentId));
        assertEquals("Ce type de document ne peut pas être prévisualisé",ex.getMessage());
    }
}
