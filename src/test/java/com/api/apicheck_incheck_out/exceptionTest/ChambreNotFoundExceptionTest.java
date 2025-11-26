package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.ChambreNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

 class ChambreNotFoundExceptionTest {
    @Test
    void chambreNotFoundExceptionMessage() {
        String message = "Chambre not found with ID 1";
        ChambreNotFoundException exception = assertThrows(
                ChambreNotFoundException.class,
                () -> { throw new ChambreNotFoundException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
