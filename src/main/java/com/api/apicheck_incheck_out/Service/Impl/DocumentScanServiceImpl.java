package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.Service.DocumentScanService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.io.InputStream;

@Service
public class DocumentScanServiceImpl implements DocumentScanService {



    @Override
    public DocumentScanDTO uploadDocScan(MultipartFile file) {
        try {
            String extractApiUrl = "http://localhost:8000/extract-doc-info";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            InputStream inputStream = file.getInputStream();
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream) {
                @Override
                public long contentLength() throws IOException {
                    return file.getSize();
                }

                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", inputStreamResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<DocumentScanDTO> response = restTemplate.exchange(extractApiUrl, HttpMethod.POST, requestEntity, DocumentScanDTO.class);

            return response.getBody();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'envoi du fichier pour extraction!", e);
        }
    }


}

