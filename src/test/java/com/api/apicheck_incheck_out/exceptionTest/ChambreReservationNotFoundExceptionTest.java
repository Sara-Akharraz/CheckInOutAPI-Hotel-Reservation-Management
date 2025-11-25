package com.api.apicheck_incheck_out.exceptionTest;

import com.api.apicheck_incheck_out.exceptionhandling.ChambreReservationNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChambreReservationNotFoundExceptionTest {
    @Test
    void ChambreReservationNotFoundExceptionMessage() {
        String message = "Chambre reservation not found with ID 1";

        ChambreReservationNotFoundException exception = assertThrows(
                ChambreReservationNotFoundException.class,
                () -> { throw new ChambreReservationNotFoundException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
