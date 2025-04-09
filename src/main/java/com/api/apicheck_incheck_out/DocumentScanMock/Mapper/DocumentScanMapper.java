package com.api.apicheck_incheck_out.DocumentScanMock.Mapper;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Entity.DocumentScan;
import org.springframework.stereotype.Component;

@Component
public class DocumentScanMapper {
    public DocumentScanDTO toDTO(DocumentScan documentScan){
        return new DocumentScanDTO(
                documentScan.getId(),
                documentScan.getNom(),
                documentScan.getPrenom(),
                documentScan.getType(),
                documentScan.getCin(),
                documentScan.getPassport()
        );
    }
}
