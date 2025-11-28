package com.api.apicheck_incheck_out.dtotest;

import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentScanDTOTest {
    @Test
    void testDocumentScanDTO(){
        //allArgsConstuctor
        DocumentScanDTO doc1=new DocumentScanDTO(1L,"Akharraz","Sara", DocumentScanType.CIN,"ABC123",null,new byte[]{1,2,3},"test.jpg","image/jpeg");
        DocumentScanDTO doc2=new DocumentScanDTO(1L,"Akharraz","Sara", DocumentScanType.CIN,"ABC123",null,new byte[]{1,2,3},"test.jpg","image/jpeg");
        DocumentScanDTO doc3=new DocumentScanDTO(3L,"Akharraz","Sara", DocumentScanType.PASSPORT,null,"ABC123",new byte[]{1,2,3},"test.jpg","image/jpeg");
        //getId
        assertEquals(1L,doc1.getId());
        //equals
        assertEquals(doc1,doc2);
        assertNotEquals(doc1,doc3);
        //hashCode
        assertEquals(doc1.hashCode(),doc2.hashCode());
        assertNotEquals(doc1.hashCode(),doc3.hashCode());
        //setId
        doc1.setId(100L);
        //ToString
        assertNotNull(doc1.toString());
    }
}
