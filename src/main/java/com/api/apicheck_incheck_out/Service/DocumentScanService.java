package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentScanService {
//    public DocumentScanDTO saveDocScanMock(DocumentScanDTO doc);
//    public DocumentScanDTO getDocScanMock(Long id);
//    //comme s'il a repeté le scan pls fois
//    public DocumentScanDTO updateDocScanMock(Long id,DocumentScanDTO doc);
    public DocumentScanDTO uploadDocScan(MultipartFile file);
}
