package com.api.apicheck_incheck_out.exceptionTest;

import com.api.apicheck_incheck_out.exceptionhandling.CheckOutNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckOutNotFoundExceptionTest {
    @Test
    void checkOutNotFoundExceptionMessage() {
        String message = "Check-Out not found with ID 1";

        CheckOutNotFoundException exception = assertThrows(
                CheckOutNotFoundException.class,
                () -> { throw new CheckOutNotFoundException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
