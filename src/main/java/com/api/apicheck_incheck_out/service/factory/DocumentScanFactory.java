package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.repository.DocumentScanRepository;
import org.springframework.stereotype.Component;

@Component
public class DocumentScanFactory {
    private final DocumentScanRepository documentScanRepository;

    public DocumentScanFactory(DocumentScanRepository documentScanRepository) {
        this.documentScanRepository = documentScanRepository;
    }

    public DocumentScan createAndSave(DocumentScanDTO dto) {
        DocumentScan documentScan = new DocumentScan();
        documentScan.setNom(dto.getNom());
        documentScan.setPrenom(dto.getPrenom());
        documentScan.setCin(dto.getCin());
        documentScan.setPassport(dto.getPassport());
        documentScan.setType(dto.getType());
        documentScan.setImage(dto.getImage());
        documentScan.setFileName(dto.getFileName());
        documentScan.setFileType(dto.getFileType());

        return documentScanRepository.save(documentScan);
    }
}
