package com.api.apicheck_incheck_out.exceptionTest;

import com.api.apicheck_incheck_out.exceptionhandling.FactureNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FactureNotFoundExceptionTest {



    @Test
    void factureNotFoundExceptionMessage() {
        String message = "Facture not found with ID 1";
        FactureNotFoundException exception = assertThrows(
                FactureNotFoundException.class,
                () -> { throw new FactureNotFoundException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
