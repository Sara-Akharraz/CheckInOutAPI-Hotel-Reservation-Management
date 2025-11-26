package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.DocumentNotScannedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

 class DocumentNotScannedExceptionTest {

    @Test
    void documentNotScannedExceptionMessage() {
        String message = "Document not scanned";

        DocumentNotScannedException exception = assertThrows(
                DocumentNotScannedException.class,
                () -> { throw new DocumentNotScannedException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
