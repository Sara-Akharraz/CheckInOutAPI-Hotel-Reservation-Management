package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.Entity.DocumentScan;
import com.api.apicheck_incheck_out.Repository.DocumentScanRepository;
import com.api.apicheck_incheck_out.Service.DocumentScanService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/mock_documents")
public class DocumentScanController {
    private final DocumentScanService documentScanService;
    private final DocumentScanRepository documentScanRepository;

    public DocumentScanController(DocumentScanService documentScanService, DocumentScanRepository documentScanRepository) {
        this.documentScanService = documentScanService;
        this.documentScanRepository = documentScanRepository;
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<DocumentScanDTO> updateDocScanMocked(@PathVariable Long id,@RequestBody DocumentScanDTO updatedDoc){
//        return ResponseEntity.ok(documentScanService.updateDocScanMock(id,updatedDoc));
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<DocumentScanDTO> getDocScanMocked(@PathVariable Long id){
//        DocumentScanDTO doc=documentScanService.getDocScanMock(id);
//        if(doc==null){
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return ResponseEntity.ok(doc);
//    }

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
