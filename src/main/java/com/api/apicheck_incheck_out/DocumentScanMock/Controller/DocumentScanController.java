package com.api.apicheck_incheck_out.DocumentScanMock.Controller;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Service.DocumentScanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/mock_documents")
public class DocumentScanController {
    private final DocumentScanService documentScanService;

    public DocumentScanController(DocumentScanService documentScanService) {
        this.documentScanService = documentScanService;
    }
    @PostMapping
    public ResponseEntity<DocumentScanDTO> addDocScanMocked(@RequestBody DocumentScanDTO doc){
        return new ResponseEntity<>(documentScanService.saveDocScanMock(doc), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DocumentScanDTO> updateDocScanMocked(@PathVariable Long id,@RequestBody DocumentScanDTO updatedDoc){
        return ResponseEntity.ok(documentScanService.updateDocScanMock(id,updatedDoc));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DocumentScanDTO> getDocScanMocked(@PathVariable Long id){
        DocumentScanDTO doc=documentScanService.getDocScanMock(id);
        if(doc==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(doc);
    }
}
