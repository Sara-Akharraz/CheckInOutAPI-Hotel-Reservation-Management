package com.api.apicheck_incheck_out.DocumentScanMock.Service;

import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import org.apache.commons.lang3.ClassUtils;

public interface DocumentScanService {
    public DocumentScanDTO saveDocScanMock(DocumentScanDTO doc);
    public DocumentScanDTO getDocScanMock(Long id);
    //comme s'il a repeté le scan pls fois
    public DocumentScanDTO updateDocScanMock(Long id,DocumentScanDTO doc);


}
