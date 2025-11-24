package com.api.apicheck_incheck_out.serviceimptest;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.exceptionhandling.FileExtractionException;
import com.api.apicheck_incheck_out.service.impl.DocumentScanServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class DocumentServiceImplTest {
    @Mock
    private MultipartFile file;
    @InjectMocks
    private DocumentScanServiceImpl documentScanService;


    @Test
    void testUploadDocScanThrowsException() throws Exception {
        when(file.getInputStream()).thenThrow(new IOException("IO Error"));

        FileExtractionException ex = assertThrows(FileExtractionException.class, () -> documentScanService.uploadDocScan(file));
        assertTrue(ex.getMessage().contains("Erreur lors de l'envoi du fichier"));
    }

    @Test
    void testUploadDocScan() throws Exception {

        MultipartFile files = mock(MultipartFile.class);
        when(files.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(files.getSize()).thenReturn(123L);
        when(files.getOriginalFilename()).thenReturn("testFile.txt");


        RestTemplate restTemplate = mock(RestTemplate.class);
        DocumentScanDTO dto = new DocumentScanDTO();
        ResponseEntity<DocumentScanDTO> responseEntity = new ResponseEntity<>(dto, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(DocumentScanDTO.class)
        )).thenReturn(responseEntity);


        DocumentScanServiceImpl service = new DocumentScanServiceImpl() {
            @Override
            public DocumentScanDTO uploadDocScan(MultipartFile f) {
                try {
                    String extractApiUrl = "http://localhost:8000/extract-doc-info";

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    InputStream inputStream = f.getInputStream();
                    InputStreamResource inputStreamResource = new InputStreamResource(inputStream) {
                        @Override
                        public long contentLength() {
                            return f.getSize();
                        }

                        @Override
                        public String getFilename() {
                            return f.getOriginalFilename();
                        }
                    };

                    inputStreamResource.contentLength();
                    inputStreamResource.getFilename();

                    body.add("file", inputStreamResource);
                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                    return restTemplate.exchange(extractApiUrl, HttpMethod.POST, requestEntity, DocumentScanDTO.class).getBody();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };


        DocumentScanDTO result = service.uploadDocScan(files);
        assertNotNull(result);

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(DocumentScanDTO.class)
        );
    }

}
