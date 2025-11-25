package com.api.apicheck_incheck_out.exceptionTest;

import com.api.apicheck_incheck_out.exceptionhandling.FileExtractionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileExtractionExceptionTest {

    @Test
    void fileExtractionExceptionMessage() {
        String message = "Failed to extract file";
        Throwable cause = new RuntimeException("Underlying IO error");

        FileExtractionException exception = assertThrows(
                FileExtractionException.class,
                () -> { throw new FileExtractionException(message, cause); }
        );

        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

}
