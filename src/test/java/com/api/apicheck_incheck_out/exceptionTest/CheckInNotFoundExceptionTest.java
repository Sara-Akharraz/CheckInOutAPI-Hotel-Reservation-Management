package com.api.apicheck_incheck_out.exceptiontest;

import com.api.apicheck_incheck_out.exceptionhandling.CheckInNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

 class CheckInNotFoundExceptionTest {
    @Test
    void checkInNotFoundExceptionMessage() {
        String message = "Check-In not found with ID 1";

        CheckInNotFoundException exception = assertThrows(
                CheckInNotFoundException.class,
                () -> { throw new CheckInNotFoundException(message); }
        );

        assertEquals(message, exception.getMessage());
    }
}
