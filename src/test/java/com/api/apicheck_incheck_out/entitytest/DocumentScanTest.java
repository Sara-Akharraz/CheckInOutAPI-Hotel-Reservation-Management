package com.api.apicheck_incheck_out.entitytest;

import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class DocumentScanTest {
    @Test
    void testDocumentScan(){
        CheckIn checkIn=new CheckIn();
        checkIn.setId(1L);

        byte[] image=new byte[]{1,2,3};
        DocumentScan doc= new DocumentScan(
                10L,
                "Akharraz",
                "Sara",
                "CIN123",
                "PASS123",
                DocumentScanType.CIN,
                checkIn,
                image,
                "file1.jpg",
                "image/jpeg"
        );
        assertEquals("Akharraz", doc.getNom());
        assertEquals("Sara", doc.getPrenom());
        assertEquals("CIN123", doc.getCin());
        assertEquals("PASS123", doc.getPassport());
        assertEquals(DocumentScanType.CIN, doc.getType());
        assertEquals(checkIn, doc.getCheckIn());
        assertEquals("file1.jpg", doc.getFileName());
        assertEquals("image/jpeg", doc.getFileType());
        assertArrayEquals(image, doc.getImage());

        doc.setId(10L);
        assertEquals(10L,doc.getId());

        DocumentScan doc2 = new DocumentScan(
                10L,
                "Akharraz",
                "Sara",
                "CIN123",
                "PASS123",
                DocumentScanType.CIN,
                checkIn,
                image,
                "file1.jpg",
                "image/jpeg"
        );
        DocumentScan doc3 = new DocumentScan(
                30L,
                "Akharraz",
                "Salma",
                "CIN999",
                "PASS999",
                DocumentScanType.PASSPORT,
                null,
                new byte[]{4,5,6},
                "file2.png",
                "image/png"
        );
        assertEquals(doc, doc2);
        assertNotEquals(doc, doc3);
        assertEquals(doc.hashCode(), doc2.hashCode());
        assertNotEquals(doc.hashCode(), doc3.hashCode());

        String s = doc.toString();
        assertTrue(s.contains("Akharraz") || s.contains("CIN123"));


    }
}
