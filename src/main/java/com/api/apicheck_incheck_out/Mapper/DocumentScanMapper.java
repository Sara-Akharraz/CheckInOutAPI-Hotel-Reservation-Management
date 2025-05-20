package com.api.apicheck_incheck_out.Mapper;

import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.Entity.DocumentScan;
import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentScanMapper {
    @Autowired
    CheckInRepository checkInRepository;
    public DocumentScanDTO toDTO(DocumentScan documentScan){
        return new DocumentScanDTO(
                documentScan.getId(),
                documentScan.getNom(),
                documentScan.getPrenom(),
                documentScan.getType(),
                documentScan.getCin(),
                documentScan.getPassport(),
                documentScan.getImage(),
                documentScan.getFileName(),
                documentScan.getFileType()
        );
    }
    public DocumentScan ToEntity(DocumentScanDTO dto, Long idCheckin) {
        DocumentScan documentScan = new DocumentScan();

        documentScan.setId(dto.getId());
        documentScan.setNom(dto.getNom());
        documentScan.setPrenom(dto.getPrenom());
        documentScan.setCin(dto.getCin());
        documentScan.setPassport(dto.getPassport());
        documentScan.setType(dto.getType());
        documentScan.setImage(dto.getImage());
        documentScan.setFileName(dto.getFileName());
        documentScan.setFileType(dto.getFileType());

        if (idCheckin != null) {
            Check_In checkIn = checkInRepository.findById(idCheckin)
                    .orElseThrow(() -> new RuntimeException("Check_in non trouvé"));
            documentScan.setCheckIn(checkIn);
            checkIn.setDocumentScan(documentScan);
        }

        return documentScan;
    }
}
