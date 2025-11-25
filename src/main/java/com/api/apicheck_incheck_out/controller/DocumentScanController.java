package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.repository.DocumentScanRepository;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/mock_documents")
public class DocumentScanController {
    private final DocumentScanRepository documentScanRepository;

    public DocumentScanController(DocumentScanRepository documentScanRepository) {
        this.documentScanRepository = documentScanRepository;
    }


    @GetMapping(value = "/preview/{documentId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> previewDocument(@PathVariable Long documentId) {

        DocumentScan document = documentScanRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));


        if (!document.getFileType().startsWith("image/")) {
            throw new UnsupportedOperationException("Ce type de document ne peut pas être prévisualisé");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());


        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .body(document.getImage());
    }


}
