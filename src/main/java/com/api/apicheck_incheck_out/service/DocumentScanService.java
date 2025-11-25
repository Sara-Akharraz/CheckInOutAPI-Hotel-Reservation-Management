package com.api.apicheck_incheck_out.service;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentScanService {
    public DocumentScanDTO uploadDocScan(MultipartFile file);
}
