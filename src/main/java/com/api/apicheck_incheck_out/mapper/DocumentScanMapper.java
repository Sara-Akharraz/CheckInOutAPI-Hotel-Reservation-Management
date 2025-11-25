package com.api.apicheck_incheck_out.mapper;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.repository.CheckInRepository;
import org.springframework.stereotype.Component;

@Component
public class DocumentScanMapper {

    private final CheckInRepository checkInRepository;

    public DocumentScanMapper(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

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
    public DocumentScan toEntity(DocumentScanDTO dto, Long idCheckin) {
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
            CheckIn checkIn = checkInRepository.findById(idCheckin)
                    .orElseThrow(() -> new RuntimeException("Check_in non trouvé"));
            documentScan.setCheckIn(checkIn);
            checkIn.setDocumentScan(documentScan);
        }

        return documentScan;
    }
}
