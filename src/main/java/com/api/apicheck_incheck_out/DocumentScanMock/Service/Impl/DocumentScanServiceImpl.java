package com.api.apicheck_incheck_out.DocumentScanMock.Service.Impl;

import com.api.apicheck_incheck_out.DTO.ReservationDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.DocumentScanMock.Service.DocumentScanService;
import com.api.apicheck_incheck_out.Enums.DocumentScanType;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentScanServiceImpl implements DocumentScanService {
    private static final String FILE_PATH = "src/main/resources/doc_scan_mock_data.json";
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Map<Long, DocumentScanDTO> mockDocuments = new HashMap<>();
    private static Long nextId = 1L;

    private Long generateNextId() {
        return nextId++;
    }

    @PostConstruct
    public void loadMockData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                Map<Long, DocumentScanDTO> documents = objectMapper.readValue(file, new TypeReference<Map<Long, DocumentScanDTO>>() {
                });
                mockDocuments.clear();
                mockDocuments.putAll(documents);
                System.out.println("DocScan Mock Data loaded from file");
            } catch (IOException e) {
                System.err.println("Failed to load DocScan Data " + e.getMessage());
            }
        }
    }

    @PreDestroy
    public void saveMockData() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), mockDocuments);
            System.out.println("DocScan Mock Data saved to file");
        } catch (IOException e) {
            System.err.println("Failed to save DocScan Mock data " + e.getMessage());
        }
    }

    @Override
    public DocumentScanDTO saveDocScanMock(DocumentScanDTO doc) {
       if(doc.getType()== DocumentScanType.CIN && (doc.getCin()==null|| doc.getCin().isBlank())){
           throw new IllegalArgumentException("CIN requis si type est CIN.");
       }
        if(doc.getType()== DocumentScanType.PASSPORT && (doc.getPassport()==null|| doc.getPassport().isBlank())){
            throw new IllegalArgumentException("Passport requis si type est PASSPORT.");
        }
        if(doc.getType()==DocumentScanType.CIN){
            doc.setPassport(null);
        }else{
            doc.setCin(null);
        }

        Long documentId = generateNextId();
        doc.setId(documentId);
        mockDocuments.put(documentId, doc);
        saveMockData();
        return doc;
    }

    @Override
    public DocumentScanDTO getDocScanMock(Long id) {
        return mockDocuments.get(id);
    }

    @Override
    public DocumentScanDTO updateDocScanMock(Long id, DocumentScanDTO updatedDoc) {
        DocumentScanDTO doc = mockDocuments.get(id);
        if(doc==null){
            throw new RuntimeException("DocScan Mock introuvable pour l'id "+id);
        }
            doc.setNom(updatedDoc.getNom());
            doc.setType(updatedDoc.getType());
            doc.setCin(updatedDoc.getCin());
            doc.setPrenom(updatedDoc.getPrenom());
            doc.setPassport(updatedDoc.getPassport());

        saveMockData();
        return doc;
    }
}

