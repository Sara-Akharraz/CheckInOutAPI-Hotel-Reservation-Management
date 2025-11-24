package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.PDFGenerationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PDFGenerationExceptionTest {
    @Test
    void testPDFGenerationException(){
        Throwable cause=new RuntimeException("cause");
        PDFGenerationException exception=new PDFGenerationException("erreur",cause);
        assertEquals("erreur",exception.getMessage());
        assertEquals(cause,exception.getCause());

    }
}
